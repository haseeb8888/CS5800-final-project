/**
 * This class provides a simple interface for zipping and unzipping files.
 *
 * @author Haseeb
 */
public class ZipUnzip {

  /**
   * The main method of the ZipUnzip class. It prompts the user to enter a file name and an option,
   * and then performs either file zipping or unzipping.
   *
   * @param args The command-line arguments.
   */
  public static void main(String[] args) {

    ZipUnzipController controller = new ZipUnzipController();
    controller.run();
  }

}