/*
package com.mpsdevelopment.biopotential.server.cmp.pcm;

import com.mpsdevelopment.plasticine.commons.ClasspathResourceManager;
import com.mpsdevelopment.plasticine.commons.LogbackConfigureLoader;
import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;
import org.lwjgl.LWJGLException;
import org.lwjgl.openal.*;
import org.lwjgl.system.MemoryUtil;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

import static com.sun.el.lang.ELArithmetic.add;

public class HardyExample {

	private static ClasspathResourceManager resourceManager = ClasspathResourceManager.getResourceManager();
	private static final Logger LOGGER = LoggerUtil.getLogger(HardyExample.class);

	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {

		LogbackConfigureLoader.initializeLogging(resourceManager, "logback_sound.xml", "jul.properties");
		String path = String.format("%s/nativelib/lwjgl3", System.getProperty("user.dir"));

		if (!new File(path).exists()) {
			LOGGER.info("Path for sound libraries does not work: %s", path);
			path = String.format("%s", System.getProperty("user.dir"));
			if (!new File(path).exists()) {
				LOGGER.info("Path for sound libraries does not work: %s", path);
				path = System.getProperty("user.dir");
			}
		}

		LOGGER.info("Path for sound libraries: %s", path);
		System.setProperty("org.lwjgl.librarypath", path);

		new HardyExample().execute();
	}

	protected void execute() throws UnsupportedEncodingException {
//		createMenuBars();
		try {
			AL.create();

//			 AL.create("Speakers (USB Audio Device)", 44100, 60, false);
			checkForErrors();
		} catch (LWJGLException le) {
			error("Init", le.getMessage());
		}
		printALCInfo();
		checkForErrors();

		AL.destroy();
	}

	private void printALCInfo() throws UnsupportedEncodingException {
		*/
/*Mixer.Info[] mixerInfo = AudioSystem.getMixerInfo();

		for(int i = 0; i < mixerInfo.length; i++)
		{
			System.out.println(new String(mixerInfo[i].getName().getBytes("Windows-1252"), "Windows-1251"));
        }*//*


        //Enumerates all available microphones
        */
/*Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();
        for (Mixer.Info info: mixerInfos){
            Mixer m = AudioSystem.getMixer(info);
            Line.Info[] lineInfos = m.getTargetLineInfo();
            if(lineInfos.length>=1 && lineInfos[0].getLineClass().equals(TargetDataLine.class)){//Only prints out info is it is a Microphone
                System.out.println("Line Name: " + info.getName());//The name of the AudioDevice
                System.out.println("Line Description: " + info.getDescription());//The type of audio device
                for (Line.Info lineInfo:lineInfos){
                    System.out.println ("\t"+"---"+lineInfo);
                    Line line;
                    try {
                        line = m.getLine(lineInfo);
                    } catch (LineUnavailableException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        return;
                    }
                    System.out.println("\t-----"+line);
                }
            }
        }*//*



		ALCdevice device;

//        ALCdevice device = AL.getDevice();
		if (ALC10.alcIsExtensionPresent(null, "ALC_ENUMERATION_EXT")) {
			if (ALC10.alcIsExtensionPresent(null, "ALC_ENUMERATE_ALL_EXT")) {
				printDevices(ALC11.ALC_ALL_DEVICES_SPECIFIER, "playback");
			} else {
				printDevices(ALC10.ALC_DEVICE_SPECIFIER, "playback");
			}
		} else {
			LOGGER.info("No device enumeration available");
		}

		device = ALC10.alcGetContextsDevice(ALC10.alcGetCurrentContext());
		checkForErrors();

		System.out.println("Default playback device: " + ALC10.alcGetString(device, ALC10.ALC_DEFAULT_DEVICE_SPECIFIER));
	}

	private void printDevices(int which, String kind) {
        System.out.println("Available " + ALC10.alcGetString(null, which));
		String[] devices = ALC10.alcGetString(null, which).split("\\u0000");
		checkForErrors();

        System.out.println("Available %s devices: " + kind);
		for (String device : devices) {
            System.out.println("    " + device);
		}
	}

	private void checkForErrors() {
		{
			ALCdevice device = ALC10.alcGetContextsDevice(ALC10.alcGetCurrentContext());
			int error = ALC10.alcGetError(device);
			if (error != ALC10.ALC_NO_ERROR) {
				error("ALC", ALC10.alcGetString(device, error));
			}
		}
		{
			int error = AL10.alGetError();
			if (error != AL10.AL_NO_ERROR) {
				error("AL", AL10.alGetString(error));
			}
		}
	}

	private void error(String kind, String description) {
		LOGGER.error(" %s error %s occurred", kind, description);
	}

	private void createMenuBars(){
		JMenuBar menuBar = new JMenuBar();
		menuBar.setBounds(0, 0, 60, 20);

		JMenu optionMenu = new JMenu("Options");
		JMenuItem pickMixers = new JMenuItem("Select a Playback Path");
		optionMenu.add(pickMixers);
		optionMenu.addSeparator();

		ButtonGroup mixerSelections = new ButtonGroup();

		addMixerOption("default sound system", mixerSelections, optionMenu, true);

		AudioFormat audioFmt = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
				44100, 16, 2, 4, 44100, false);
		Mixer.Info[] mixers = AudioSystem.getMixerInfo();
		for (Mixer.Info info : mixers)
		{
			Mixer mixer = AudioSystem.getMixer(info);

			try
			{
//          System.out.println(info);
				DataLine.Info sdlLineInfo = new DataLine.Info(SourceDataLine.class, audioFmt);

				// test if line is assignable
				@SuppressWarnings("unused")
				SourceDataLine sdl = (SourceDataLine) mixer.getLine(sdlLineInfo);

				// if successful, add to list
				addMixerOption(info.getName() + " <> " + info.getDescription(),
						mixerSelections, optionMenu, false);
			}
			catch (LineUnavailableException e)
			{
				//e.printStackTrace();
				System.out.println("Mixer rejected, Line Unavailable: " + info);
			}
			catch (IllegalArgumentException e)
			{
				//e.printStackTrace();
				System.out.println("Mixer rejected, Illegal Argument: " + info);
			}
		}

		menuBar.add(optionMenu);
//		add(menuBar,0);
	}

	private void addMixerOption(String optionName, ButtonGroup bg,
								JMenu menu, boolean isSelected)
	{
		JRadioButtonMenuItem newOption = new JRadioButtonMenuItem(optionName);
		bg.add(newOption);
		newOption.setSelected(isSelected);
		menu.add(newOption);
		newOption.addActionListener(new OptionListener());
		newOption.setActionCommand(optionName);
	}

	public class OptionListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent arg0)
		{
			String optionName = arg0.getActionCommand();
			Boolean defaultMixer = true;

			Mixer.Info[] mixers = AudioSystem.getMixerInfo();
			Mixer appMixer;
			for (Mixer.Info info : mixers)
			{
				if (optionName.equals(info.getName()+" <> "+info.getDescription()))
				{
					System.out.println("Option selected >  " + info.getName());
					System.out.println("    description >  " + info.getDescription());
					System.out.println("          class >  " + info.getClass());

					appMixer = AudioSystem.getMixer(info);
					System.out.println(appMixer);
					defaultMixer = false;
				}
			}
			if (defaultMixer)
			{
				System.out.println("Using default mixer, whatever that is...");
				appMixer = null;
			}
		}
	}
}*/
