/**

 Represents a node in a Huffman tree used for file compression and decompression.
 */
public class HuffmanNode {
  // The character value of the node
  int character;

  // The frequency or weight of the node
  int freq;

  /**

   Constructor for creating a Huffman node with given character and frequency values.
   @param character the character value of the node
   @param freq the frequency or weight of the node
   */
  public HuffmanNode(int character, int freq) {
    this.character = character;
    this.freq = freq;
  }
}




