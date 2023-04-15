/**
 * A HuffmanCodec class represents Binary tree for storing trees
 */
public class HuffmanCodec {
  /**
   * An array of Node objects, used to build the Huffman tree for encoding and decoding.
   */
  public Node[] nodes;

  /**
   * Constructor to initialize the HuffmanCodec object with a given size.
   *
   * @param size The size of the Node array.
   */
  public HuffmanCodec(int size) {
    nodes = new Node[size];
    for (int i = 0; i < size; i++) {
      nodes[i] = new Node();
    }
  }

  /**
   * A nested class representing a Node object in the Huffman tree.
   */
  class Node {

    /**
     * The index of the left child node in the Node array.
     */
    public int left = -1;

    /**
     * The index of the right child node in the Node array.
     */
    public int right = -1;

    /**
     * The index of the parent node in the Node array.
     */
    public int parent = -1;

  }

}
