/*
 * CubeRoll - Throw dice to play something.
 * Copyright (C) 2019 Mr_Krab
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * CubeRoll is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
 
package mr_krab.cuberoll;

import java.util.Locale;
import java.util.Map;
import java.util.Random;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GamePostInitializationEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;

import mr_krab.localeapi.LocaleAPIMain;
import mr_krab.localeapi.utils.LocaleAPI;
import mr_krab.localeapi.utils.LocaleUtil;

@Plugin(id = "cuberoll",
		name = "CubeRoll",
		version = "1.0.0-S7.1",
		dependencies = {
				@Dependency(id = "localeapi", optional = true)
		},
		authors = "Mr_Krab")
public class Main {
	private LocaleAPI localeAPI = null;

	public LocaleAPI getLocaleAPI() {
		return localeAPI;
	}

	@Listener
	public void onPostInitialization(GamePostInitializationEvent event) {
		localeAPI = LocaleAPIMain.getInstance().getAPI();
		localeAPI.saveLocales("cuberoll");
		commandRegister();
	}

	public void commandRegister() {
		CommandSpec commandRoll = CommandSpec.builder()
				.permission("cuberoll.roll")
				.arguments(GenericArguments.optional(GenericArguments.integer(Text.of("MaxValue"))))
    	        .executor((src, args) -> {
    	        	if(!(src instanceof Player)) {
        	            throw new CommandException(getDefaultLocale().getString("only-player"));
    	        	}
    	        	if(args.<Integer>getOne(Text.of("MaxValue")).isPresent()) {
    	        		Random random = new Random();
    	        		int maxValue = args.<Integer>getOne(Text.of("MaxValue")).get();
    	        		int value = random.nextInt(maxValue + 1);
    	        		MessageChannel.TO_CONSOLE.send(getDefaultLocale()
    	        				.getString("command-roll", src.getName(), String.valueOf(value), String.valueOf(maxValue)));
    	        		for(Player player : Sponge.getServer().getOnlinePlayers()) {
    	        			player.sendMessage(getOrDefaultLocale(player.getLocale())
    	        					.getString("command-roll", src.getName(), String.valueOf(value), String.valueOf(maxValue)));
    	        		}
        	            return CommandResult.success();
    	        	}
	        		Random random = new Random();
	        		int value = random.nextInt(101);
	        		MessageChannel.TO_CONSOLE.send(getDefaultLocale()
	        				.getString("command-roll", src.getName(), String.valueOf(value), "100"));
	        		for(Player player : Sponge.getServer().getOnlinePlayers()) {
	        			player.sendMessage(getOrDefaultLocale(player.getLocale())
	        					.getString("command-roll", src.getName(), String.valueOf(value), "100"));
	        		}
    	            return CommandResult.success();
    	        })
    	        .build();

		CommandSpec commandTry = CommandSpec.builder()
				.permission("cuberoll.try")
				.arguments(GenericArguments.optional(GenericArguments.remainingJoinedStrings(Text.of("Text"))))
    	        .executor((src, args) -> {
    	        	if(!(src instanceof Player)) {
        	            throw new CommandException(getDefaultLocale().getString("only-player"));
    	        	}
    	        	if(!args.<String>getOne(Text.of("Text")).isPresent()) {
        	            throw new CommandException(getOrDefaultLocale(src.getLocale()).getString("empty-message"));
    	        	}
        			String message = args.<String>getOne(Text.of("Text")).get();
	        		Random random = new Random();
	        		int value = random.nextInt(2);
	        		if(value == 0) {
	        			for(Player player : Sponge.getServer().getOnlinePlayers()) {
	        				Locale locale = player.getLocale();
	        				player.sendMessage(getOrDefaultLocale(locale)
	        						.getString("command-try", src.getName(), message, getOrDefaultLocale(locale).getLegacyString("failure", true)));
	        			}
	        			MessageChannel.TO_CONSOLE.send(getDefaultLocale()
	        					.getString("command-try", src.getName(), message, getDefaultLocale().getLegacyString("failure", true)));
	        		}
	        		if(value == 1) {
	        			for(Player player : Sponge.getServer().getOnlinePlayers()) {
	        				Locale locale = player.getLocale();
	        				player.sendMessage(getOrDefaultLocale(locale)
	        						.getString("command-try", src.getName(), message, getOrDefaultLocale(locale).getLegacyString("luck", true)));
	        			}
	        			MessageChannel.TO_CONSOLE.send(getDefaultLocale()
	        					.getString("command-try", src.getName(), message, getDefaultLocale().getLegacyString("luck", true)));
	        		}
    	            return CommandResult.success();
    	        })
				.build();
		
		Sponge.getCommandManager().register(this, commandRoll, "roll");
		Sponge.getCommandManager().register(this, commandTry, "try");
	}

	public Map<Locale, LocaleUtil> getLocales() {
		return localeAPI.getLocalesMap("cuberoll");
	}

	public LocaleUtil getLocale(Locale locale) {
		return getLocales().get(locale);
	}

	public LocaleUtil getDefaultLocale() {
		return getLocales().get(localeAPI.getDefaultLocale());
	}

	public LocaleUtil getOrDefaultLocale(Locale locale) {
		if(getLocales().containsKey(locale)) {
			return getLocale(locale);
		}
		return getDefaultLocale();
	}

}