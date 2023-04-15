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
   * @param weights The weights of the characters in the original data.
   */
  public ZipUnzipStats(int[] weights) {
    huffmanTreeNodes = new HuffmanNode[weights.length];
    for (int i = 0; i < weights.length; i++) {
      huffmanTreeNodes[i] = new HuffmanNode(i, weights[i]);
    }
  }

}