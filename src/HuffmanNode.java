/**

 Represents a node in a Huffman tree used for file compression and decompression.
 */
public class HuffmanNode {
  // The character value of the node
  int character;

  // The frequency or weight of the node
  int frequency;

  /**

   Constructor for creating a Huffman node with given character and frequency values.
   @param holder the character value of the node
   @param weight the frequency or weight of the node
   */
  public HuffmanNode(int holder, int weight) {
    this.character = holder;
    this.frequency = weight;
  }
}




