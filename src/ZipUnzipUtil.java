import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

public class ZipUnzipUtil {

  char[] letters;
  int[] frequency;
  char[][] binaryCodes;
  HuffmanCodec tree;
  ZipUnzipStats huffmanTrees;
  int n, lastTree, lastNode;

  // huffman empty constructor
  public ZipUnzipUtil() {
  }

  // create the forest
  private void createNewNodeAndUpdateHuffmanTree(int index1, int index2) {
    int newCharacter = lastNode;
    int leftChild = huffmanTrees.huffmanTreeNodes[index1].character;
    int rightChild = huffmanTrees.huffmanTreeNodes[index2].character;
    int newFrequency = huffmanTrees.huffmanTreeNodes[index1].freq + huffmanTrees.huffmanTreeNodes[index2].freq;

    // Set the parents of the two nodes to the new node.
    setParent(leftChild, newCharacter);
    setParent(rightChild, newCharacter);

    // Set the left and right children of the new node.
    tree.nodes[newCharacter].left = leftChild;
    tree.nodes[newCharacter].right = rightChild;

    // Update the character and frequency of the second node.
    huffmanTrees.huffmanTreeNodes[index2].character = newCharacter;
    huffmanTrees.huffmanTreeNodes[index2].freq = newFrequency;

    // Remove the first node from the huffman tree list.
    huffmanTrees.huffmanTreeNodes[index1] = huffmanTrees.huffmanTreeNodes[lastTree];

    // If the new node is not the last node in the huffman tree list,
    // set its parent to the next node.
    if (newCharacter != 2 * (n - 1)) {
      tree.nodes[newCharacter].parent = newCharacter + 1;
    }

    // Increment the last node index.
    lastNode++;
  }

  private void setParent(int child, int parent) {
    tree.nodes[child].parent = parent;
  }



  // find two minimum nodes
  private void huffman() {
    int[] mindiff = new int[2];
    findIndicesOfSmallestAndSecondSmallestFrequencies(mindiff);
    createNewNodeAndUpdateHuffmanTree(mindiff[0], mindiff[1]);
  }

  // find the minimum among two nodes
  private void findIndicesOfSmallestAndSecondSmallestFrequencies(int[] res) {
    int smallestIndex, secondSmallestIndex;

    // Initialize smallest and second smallest indices
    if (huffmanTrees.huffmanTreeNodes[0].freq <= huffmanTrees.huffmanTreeNodes[1].freq) {
      smallestIndex = 0;
      secondSmallestIndex = 1;
    } else {
      smallestIndex = 1;
      secondSmallestIndex = 0;
    }

    // Update smallest and second smallest indices
    for (int i = 2; i <= lastTree; i++) {
      if (huffmanTrees.huffmanTreeNodes[i].freq < huffmanTrees.huffmanTreeNodes[smallestIndex].freq) {
        secondSmallestIndex = smallestIndex;
        smallestIndex = i;
      } else if (huffmanTrees.huffmanTreeNodes[i].freq < huffmanTrees.huffmanTreeNodes[secondSmallestIndex].freq) {
        secondSmallestIndex = i;
      }
    }

    // Store the indices in the result array
    res[0] = smallestIndex;
    res[1] = secondSmallestIndex;
  }


  // generate the binary codes for the input text file.
  char[] generateBinaryCode(char character) {
    int characterIndex = findCharacterIndex(character);
    if (characterIndex != -1) {
      return binaryCodes[characterIndex];
    }
    return null;
  }

  private int findCharacterIndex(char character) {
    for (int i = 0; i < letters.length; i++) {
      if (letters[i] == character) {
        return i;
      }
    }
    return -1;
  }


  // create the tree for input file.
  public void createHuffmanTree(char[] characters, int[] weights) {
    initialize(characters, weights);
    buildHuffmanTree();
    storeBinaryCodes();
  }

  private void initialize(char[] characters, int[] weights) {
    this.letters = characters;
    this.frequency = weights;
    n = characters.length;
    lastNode = n;
    lastTree = n - 1;
    binaryCodes = new char[n][];
    huffmanTrees = new ZipUnzipStats(weights);
    tree = new HuffmanCodec(n * 2 - 1);
  }

  private void buildHuffmanTree() {
    while (lastTree >= 1) {
      huffman();
      lastTree--;
    }
  }

  private void storeBinaryCodes() {
    storeBinCode();
  }


  // store the binary codes
  private void storeBinCode() {
    for (int i = 0; i < n; i++) {
      binaryCodes[i] = getBinaryCode(i);
    }
  }

  private char[] getBinaryCode(int index) {
    char[] flipBinaryCodes = new char[n - 1];
    int j = index, k = 0;
    while (tree.nodes[j].parent != -1) {
      flipBinaryCodes[k] = (tree.nodes[tree.nodes[j].parent].left == j) ? '0' : '1';
      j = tree.nodes[j].parent;
      k++;
    }

    char[] binaryCode = new char[k];
    j = 0;
    while (k > 0) {
      binaryCode[j] = flipBinaryCodes[k - 1];
      k--;
      j++;
    }
    return binaryCode;
  }


  // file compression
  public void fileZipper(char[] charContent, String fileName) {
    try (DataOutputStream stream = new DataOutputStream(new FileOutputStream(fileName))) {
      // Write the length of the content and the number of distinct characters
      stream.writeInt(charContent.length);
      stream.writeInt(letters.length);

      // Write the characters and their frequencies
      for (int i = 0; i < letters.length; i++) {
        stream.writeChar(letters[i]);
        stream.writeInt(frequency[i]);
      }

      // Compress the content and write it to the output stream
      int bitCount = 0;
      byte b = 0;
      for (int i = 0; i < charContent.length; i++) {
        char[] code = generateBinaryCode(charContent[i]);
        for (int j = 0; j < code.length; j++) {
          if (code[j] == '1') {
            b |= 0x01;
          }
          if (bitCount == 7) {
            stream.writeByte(b);
            b = 0;
            bitCount = 0;
          } else {
            b <<= 1;
            bitCount++;
          }
        }
      }
      if (bitCount != 0) {
        b <<= 7 - bitCount;
        stream.writeByte(b);
      }
    } catch (IOException e) {
      System.err.println("Error zipping file: " + e.getMessage());
    }
  }


  // read a compressed file and uncompress it to original
  public void fileUnzipper(String fileName) {
    StringBuilder builder = new StringBuilder();
    int contentLength = 0;
    try {
      if (!fileName.endsWith(".zip")) {
        throw new IllegalArgumentException("File must have .zip extension");
      }
      File file = new File(fileName);
      try (DataInputStream stream = new DataInputStream(new FileInputStream(file))) {
        contentLength = stream.readInt();
        int length = stream.readInt();
        char[] chars = new char[length];
        int[] weights = new int[length];
        for (int i = 0; i < length; i++) {
          chars[i] = stream.readChar();
          weights[i] = stream.readInt();
        }
        createHuffmanTree(chars, weights);
        while (stream.available() != 0) {
          byte b = stream.readByte();
          int v = Byte.toUnsignedInt(b);
          String binaryValue = Integer.toBinaryString(v);
          while (binaryValue.length() < 8) {
            binaryValue = "0" + binaryValue;
          }
          builder.append(binaryValue);
        }
      }
    } catch (Exception e) {
      System.out.println("Unexpected error: " + e.getMessage());
      return;
    }
      if (builder.length() > 0) {
        String content = builder.toString();
        StringBuilder uncompressed = new StringBuilder();
        String[] codes = new String[binaryCodes.length];
        for (int i = 0; i < codes.length; i++) {
          codes[i] = String.valueOf(binaryCodes[i]);
        }
        int start = 0;
        while (start < content.length() && contentLength != uncompressed.length()) {
          for (int i = 0; i < codes.length; i++) {
            if (content.startsWith(codes[i], start)) {
              uncompressed.append(letters[i]);
              start += codes[i].length();
            }
          }
        }
        try (PrintWriter writer = new PrintWriter(fileName.replace(".zip", ""))) {
          writer.append(uncompressed.toString());
        } catch (IOException ex) {
          ex.printStackTrace();
        }
      } else {
        System.out.println("File is empty");
      }

  }


  // compression ratio statistics
  public void printZipStats(char[] charContent) {
    System.out.println("***************************************");
    System.out.println("Zipping Statistics:");
    System.out.println("Original file size: [ " + (charContent.length) + " bytes ]");
    int bitCount = 0;
    for (int i = 0; i < charContent.length; i++) {
      char[] code = generateBinaryCode(charContent[i]);
      for (int j = 0; j < code.length; j++) {
        bitCount++;
      }
    }
    System.out.println("After zip, new zipped file size: [ " + (bitCount / 8) + " bytes ]");
    System.out.println("Zip ratio: " + (int) ((bitCount) / (8.0 * charContent.length) * 100) + " %");
    System.out.println("***************************************");
  }

}
