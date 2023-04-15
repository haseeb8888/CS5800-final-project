/**
 * This class stores the statistics of a compression operation.
 *
 * @author Haseeb
 */
public class ZipUnzipStats {

  /**
   * The Huffman nodes of the compressed data.
   */
  public HuffmanNode[] huffmanTreeNodes;

  /**
   * Constructs a new CompressionStats object.
   *
   * @param characterWeights The weights of the characters in the original data.
   */
  public ZipUnzipStats(int[] characterWeights) {
    huffmanTreeNodes = new HuffmanNode[characterWeights.length];
    for (int i = 0; i < characterWeights.length; i++) {
      huffmanTreeNodes[i] = new HuffmanNode(i, characterWeights[i]);
    }
  }

}