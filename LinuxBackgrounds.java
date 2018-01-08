package com.microfocus.arthur.karl;

import java.time.LocalDateTime;
import java.io.FileWriter;
import java.io.File;
import java.io.BufferedWriter;
import java.nio.file.Paths;
import java.nio.file.Path;

public class LinuxBackgrounds {
    private static final double DISPLAY_DURATION = 180.0; // 2 minutes (120.0 seconds) is fine; 1 minute (60.0 seconds) is not
    private static final double TRANSITION_DURATION = 0.0;

    private static final boolean b_Fading = TRANSITION_DURATION > 0;

    private enum eOperatingSystem {
        e_SLES11,
        e_SLES12,
        e_RHEL7_2
    };

//    private static eOperatingSystem currentOS = eOperatingSystem.e_SLES11;
    private static eOperatingSystem currentOS = eOperatingSystem.e_SLES12;
//    private static eOperatingSystem currentOS = eOperatingSystem.e_RHEL7_2;

    public static void main(String[] args) {
        if( args.length < 1 ) {
            args = new String[] {
                    "D:\\images\\bing\\bing.xml",
                    (currentOS == eOperatingSystem.e_SLES11) ? "/usr/share/backgrounds/bing/" :
                    (currentOS == eOperatingSystem.e_SLES12) ? "/usr/share/wallpapers/bing/" :
                    (currentOS == eOperatingSystem.e_RHEL7_2) ? "/usr/share/backgrounds/bing/" :
                    "" // Not specified
            };
            // At least one argument is needed to proceed
            //return;
        }

//		// For testing the shuffler method
//		int[] nShuffler = {0, 1, 2, 3, 4, 5};
//		for( int k = 0; k < 10; k++) {
//			shuffleOrder(nShuffler.clone());
//		}

        LocalDateTime time = LocalDateTime.now();

        try {
            // Extract path from first argument
            Path xmlPath = Paths.get(args[0]);
            Path parent = xmlPath.getParent();
            String destinationPath = (args.length > 1) ? args[1] : "./";
            File folder = parent.toFile();
            File[] folderContents = folder.listFiles();

            FileWriter fstream = new FileWriter(xmlPath.toString(), false);
            final BufferedWriter output = new BufferedWriter(fstream);

            output.write(
                    "<background>\n\t<!--Start the clock at file creation-->\n\t<starttime>\n\t\t<year>" +
                            String.format("%04d",time.getYear()) +
                            "</year>\n\t\t<month>" +
                            String.format("%02d",time.getMonthValue()) +
                            "</month>\n\t\t<day>" +
                            String.format("%02d",time.getDayOfMonth()) +
                            "</day>\n\t\t<hour>" +
                            String.format("%02d",time.getHour()) +
                            "</hour>\n\t\t<minute>" +
                            String.format("%02d",time.getMinute()) +
                            "</minute>\n\t\t<second>" +
                            String.format("%02d",time.getSecond()) +
                            "</second>\n\t</starttime>\n\t\n\t<!--Display each image " +
                            (b_Fading ? "then fade it with the next" : "in turn") +
                            "-->"
            );

            // Determine the order the images will display; I would like a randomized order
            int[] showOrder = new int[folderContents.length];
//			for( int i = 0, j = 0; i < folderContents.length; i++, j++ ) { // Shows oldest first
            for( int i = 0, j = folderContents.length - 1; j >= 0; i++, j-- ) { // Shows newest first
                showOrder[i] = j;
            }
            shuffleOrder(showOrder);

            for( int i = 0; i < showOrder.length; i++ ) {
                if(!args[0].equalsIgnoreCase(folderContents[showOrder[i]].toString())) { // Ignores the generated XML file
                    output.write(
                            "\n\t<static>\n\t\t<duration>" +
                                    DISPLAY_DURATION +
                                    "</duration>\n\t\t<file>" +
                                    destinationPath +
                                    folderContents[showOrder[i]].getName().toString() +
                                    "</file>\n\t</static>\n"
                    );
                    if((i + 1) < folderContents.length && b_Fading) {
                        output.write(
                                "\t\n\t<transition type=\"overlay\">\n\t\t<duration>" +
                                        TRANSITION_DURATION +
                                        "</duration>\n\t\t<from>" +
                                        destinationPath +
                                        folderContents[showOrder[i]].getName().toString() +
                                        "</from>\n\t\t<to>" +
                                        destinationPath +
                                        folderContents[showOrder[i + 1]].getName().toString() +
                                        "</to>\n\t</transition>\n\t"
                        );
                    }
                }
            }

            output.write("\n</background>");
            output.close();
        } catch( Exception ex ) {

        }
    }

    /*   0   1   2   3   4   - Initial array
     * | 0   1  [2]  3   4   - [0]=0 switches with [2]=2
     *   2 | 1   0  [3]  4   - [1]=1 switches with [3]=3
     *   2   3 |[0]  1   4   - [2]=0 switches with [3]=0
     *   2   3   0 | 1  [4]  - [3]=1 switches with [4]=4
     *   2   3   0   4 |[1]  - [4]=1 switches with [4]=1 - last row (i=nCount-1) can always be skipped
     *   2   3   0   4   1 | - Final array: i=nCount
     */
//	private void shuffleOrder(Object [] source){
    private static void shuffleOrder(int [] source){
        int nLength = source.length;
        int nStoppingPoint = nLength - 1; // Last row can always be skipped; saves unnecessary computation
        int nCurrentRange = nLength;
        int nSwapLocation;

        int placeHolder;

        double randomDouble;
        java.util.Random randomGenerator = new java.util.Random();
        for(int i = 0; i < nStoppingPoint; i++) {
            randomDouble = randomGenerator.nextDouble();
            nSwapLocation = (int)java.lang.Math.floor(randomDouble * nCurrentRange);
            nSwapLocation = nSwapLocation + i;
            nCurrentRange--;

            placeHolder = source[i];
            source[i] = source[nSwapLocation];
            source[nSwapLocation] = placeHolder;

//			for(int j = 0; j < nLength; j++) {
//				if((j == i)||(j == nSwapLocation)) {
//					System.out.print("[" + source[j] + "]");
//				} else {
//					System.out.print(" " + source[j] + " ");
//				}
//			}
//			System.out.println();
        }
//		System.out.println();
    }
}

