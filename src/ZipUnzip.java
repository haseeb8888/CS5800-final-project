import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * This class provides a simple interface for zipping and unzipping files.
 *
 * @author Haseeb
 */
public class ZipUnzip {

  /**
   * The main method of the ZipUnzip class. It prompts the user to enter a file name and an option, and then performs either file zipping or unzipping.
   *
   * @param args The command-line arguments.
   */
  public static void main(String[] args) {

    // Initialize arrays to null values to store contents of input file
    char[] letters = null;
    int[] frequency = null;
    char[] charContent = null;

    // Prompt user to enter file name and select an option
    Scanner input = new Scanner(System.in);
    System.out.print("Enter the fileName: ");
    String fileName = input.nextLine();
    System.out.println("Select option from the below: ");
    System.out.println("1. zip a file: ");
    System.out.println("2. unzip a file: ");
    int choice = input.nextInt();

    // Perform either file zipping or unzipping based on user's selection
    switch (choice) {

      // If user selects option 1, zip the file
      case 1: {

        // Create a File object from the given file name
        File file = new File(fileName);

        try {
          // Use a Scanner object to read the contents of the file and store in StringBuilder
          try (Scanner in = new Scanner(file)) {
            StringBuilder sb = new StringBuilder();
            while (in.hasNext()) {
              sb.append(in.nextLine());
            }
            charContent = sb.toString().toCharArray();
          }

          // Create two ArrayLists to store unique characters and their frequencies
          List<Character> letterList = new ArrayList<>();
          List<Integer> frquencyList = new ArrayList<>();
          int index;

          // Iterate through the charContent array and store characters and frequencies in respective lists
          for (int i = 0; i < charContent.length; i++) {
            if ((index = letterList.indexOf(charContent[i])) >= 0) {
              frquencyList.set(index, frquencyList.get(index) + 1);
            } else {
              letterList.add(charContent[i]);
              frquencyList.add(1);
            }
          }

          // Convert ArrayLists to arrays for use in HuffmanUtility class
          letters = new char[letterList.size()];
          frequency = new int[frquencyList.size()];
          for (int i = 0; i < letterList.size(); i++) {
            letters[i] = letterList.get(i);
            frequency[i] = frquencyList.get(i);
          }
        } catch (FileNotFoundException ex) {
          // If file not found, print error message
          System.out.println("File not found");
          break;
        }

        // Create an instance of HuffmanUtility class to perform compression and decompression
        ZipUnzipUtil huffmanGen = new ZipUnzipUtil();
        huffmanGen.startHuffman(letters, frequency);
        huffmanGen.fileCompression(charContent, fileName + ".zip");
        huffmanGen.compressionStatistics(charContent);
        break;
      }

      // If user selects option 2, unzip the file
      case 2: {
        ZipUnzipUtil huffmanGen = new ZipUnzipUtil();
        huffmanGen.fileDecompression(fileName);
        break;
      }

      // Default case in case user does not select option 1 or 2
      default:
        break;
    }

  }

}