import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;

public class ZipUnzipUtil {

  char[] letters;
  int[] frequency;
  char[][] binaryCodes;
  HuffmanCodec tree;
  ZipUnzipStats forest;
  int n, lastTree, lastNode;

  // huffman empty constructor
  public ZipUnzipUtil() {
  }

  // create the forest
  private void initialize(int min0, int min1) {
    tree.nodes[forest.huffmanTreeNodes[min0].character].parent = tree.nodes[forest.huffmanTreeNodes[min1].character].parent = lastNode;
    forest.huffmanTreeNodes[min1].frequency += forest.huffmanTreeNodes[min0].frequency;
    tree.nodes[lastNode].left = forest.huffmanTreeNodes[min0].character;
    tree.nodes[lastNode].right = forest.huffmanTreeNodes[min1].character;
    forest.huffmanTreeNodes[min1].character = lastNode;
    forest.huffmanTreeNodes[min0] = forest.huffmanTreeNodes[lastTree];
    if (lastNode != 2 * (n - 1)) {
      tree.nodes[lastNode].parent = lastNode + 1;
    }
    lastNode++;
  }

  // find two minimum nodes
  private void huffman() {
    int[] twoMins = new int[2];
    findMinimumAmongTwoNodes(twoMins);
    initialize(twoMins[0], twoMins[1]);
  }

  // find the minimum among two nodes
  private void findMinimumAmongTwoNodes(int[] smallest) {
    if (forest.huffmanTreeNodes[0].frequency >= forest.huffmanTreeNodes[1].frequency) {
      smallest[0] = 1;
      smallest[1] = 0;
    } else {
      smallest[0] = 0;
      smallest[1] = 1;
    }
    for (int i = 2; i <= lastTree; i++) {
      if (forest.huffmanTreeNodes[smallest[0]].frequency > forest.huffmanTreeNodes[i].frequency) {
        smallest[1] = smallest[0];
        smallest[0] = i;
      } else if (forest.huffmanTreeNodes[smallest[1]].frequency > forest.huffmanTreeNodes[i].frequency) {
        smallest[1] = i;
      }
    }
  }

  // generate the binary codes for the input text file.
  char[] generateCode(char c) {
    for (int i = 0; i < letters.length; i++) {
      if (letters[i] == c) {
        return binaryCodes[i];
      }
    }
    return null;
  }

  // create the tree for input file.
  public void startHuffman(char[] characters, int[] weights) {
    this.letters = characters;
    this.frequency = weights;
    n = characters.length;
    lastNode = n;
    lastTree = n - 1;
    binaryCodes = new char[n][];
    forest = new ZipUnzipStats(weights);
    tree = new HuffmanCodec(n * 2 - 1);
    // untill the forest is not empty
    while (lastTree >= 1) {
      huffman();
      lastTree--;
    }
    code();
  }

  // store the binary codes
  private void code() {
    for (int i = 0; i < n; i++) {
      char[] flipBinaryCodes = new char[n - 1];
      int j = i, k = 0;
      while (tree.nodes[j].parent != -1) {
        if (tree.nodes[tree.nodes[j].parent].left == j) {
          flipBinaryCodes[k] = '0';
        } else {
          flipBinaryCodes[k] = '1';
        }
        j = tree.nodes[j].parent;
        k++;
      }

      binaryCodes[i] = new char[k];
      j = 0;
      while (k > 0) {
        binaryCodes[i][j] = flipBinaryCodes[k - 1];
        k--;
        j++;
      }
    }
  }

  // file compression
  public void fileCompression(char[] charContent, String fileName) {
    try {
      File file = new File(fileName);
      try (DataOutputStream stream = new DataOutputStream(new FileOutputStream(file))) {
        stream.writeInt(charContent.length);
        stream.writeInt(letters.length);
        for (int i = 0; i < letters.length; i++) {
          stream.writeChar(letters[i]);
          stream.writeInt(frequency[i]);
        }
        int bitCount = 0;
        byte b = 0;
        for (int i = 0; i < charContent.length; i++) {
          char[] code = generateCode(charContent[i]);
          for (int j = 0; j < code.length; j++) {
            if (code[j] == '1') {
              b |= 0x01;
            }
            if (bitCount == 7) {
              bitCount = 0;
              String binaryValue = Integer.toBinaryString(Byte.toUnsignedInt(b));
              while (binaryValue.length() < 8) {
                binaryValue = "0" + binaryValue;
              }
              stream.write(b);
              b = 0;
            } else {
              b <<= 1;
              bitCount++;

            }
          }

        }
        if (bitCount != 0) {
          b <<= 7 - bitCount;
          stream.write(b);
        }
        String binaryValue = Integer.toBinaryString(Byte.toUnsignedInt(b));
        while (binaryValue.length() < 8) {
          binaryValue = "0" + binaryValue;
        }
        stream.flush();
      }

    } catch (Exception e) {
      System.out.println(e);
    }

  }

  // read a compressed file and uncompress it to original
  public void fileDecompression(String fileName) throws IllegalArgumentException {
    StringBuilder builder = new StringBuilder();
    // string builder to save the huffman code
    int contentLength = 0;
    try {
      if(!fileName.contains(".zip")) {
        throw new IllegalArgumentException("First Compress the file before decompressing");
      }

      File file = new File(fileName);
      try (DataInputStream stream = new DataInputStream(new FileInputStream(file))) {
        // retreive meta data
        contentLength = stream.readInt();
        int length = stream.readInt();
        char[] chars = new char[length];
        int[] weights = new int[length];
        for (int i = 0; i < length; i++) {
          chars[i] = stream.readChar();
          weights[i] = stream.readInt();
        }
        // finished retreiving data

        // find huffman code from meta data
        startHuffman(chars, weights);
        // at this stage we have the huffman code
        while (stream.available() != 0) {

          byte b = stream.readByte();

          // read byte
          int v = Byte.toUnsignedInt(b);
          // its int unsiged value
          String binaryValue = Integer.toBinaryString(v);
          while (binaryValue.length() <= 8) {
            binaryValue = "0" + binaryValue;
            System.out.println(binaryValue.length());
          }
          System.out.println("binaryValue length " + binaryValue.length());
          builder.append(binaryValue);

          // obtain the binary codes as stream of characters
        }
        throw new Exception();
      }

    } catch (Exception e) {
      // System.out.println(builder.length());
      System.out.println(e.getMessage());
      if (builder.length() != 0) {

        String content = builder.toString();

        StringBuilder uncompressed = new StringBuilder();
        String[] codes = new String[this.binaryCodes.length];
        for (int i = 0; i < codes.length; i++) {
          codes[i] = String.valueOf(this.binaryCodes[i]);
        }
        int start = 0;
        while (start < content.length() && contentLength != uncompressed.length()) {
          // stop loop when start exceeds the content length
          // or when the content Length == to uncompressed length
          for (int i = 0; i < codes.length; i++) {
            // check which huffman code the sequence starts with
            // with a start offset
            // we decode the code sequencialy code by code
            if (content.startsWith(codes[i], start)) {
              uncompressed.append(letters[i]);
              // append the correponding character value of the code
              start += codes[i].length();
              System.out.println("start length" + start);
              //  add the code length to start
            }
          }
        }
        try {
          PrintWriter writer = new PrintWriter(new FileOutputStream(fileName.replace(".compressed", "")));
          // write the uncompressed string to another file
          // and remove the .compressed extension
          writer.append(uncompressed.toString());
          writer.flush();
          // Text I/O
        } catch (Exception ex) {
        }
      } else {
        System.out.println("builder empty");
      }
    }
  }

  // compression ratio statistics
  public void compressionStatistics(char[] charContent) {
    System.out.println("***************************************");
    System.out.println("Compression Statistics:");
    System.out.println("Original file size: [ " + (charContent.length) + " bytes ]");
    int bitCount = 0;
    for (int i = 0; i < charContent.length; i++) {
      char[] code = generateCode(charContent[i]);
      for (int j = 0; j < code.length; j++) {
        bitCount++;
      }
    }
    System.out.println("After compression, new compressed file size: [ " + (bitCount / 8) + " bytes ]");
    System.out.println("Compression ratio: " + (int) ((bitCount) / (8.0 * charContent.length) * 100) + " %");
    System.out.println("***************************************");
  }

}
