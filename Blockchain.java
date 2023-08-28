import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.io.IOException; // Import the IOException class to handle errors
import java.io.FileWriter;

//Class for a block
class Block {
    // previous hash of the block
    String previousHash;
    // found hash of the block
    String hash;
    // nonce for the block
    String nonce;
    // miner name
    private String miner;
    // difficulty for the block
    Integer difficulty;

    /*
     * Constructor for the block class that sets variables and starts mining process
     * 
     * @param String previous hash
     * 
     * @param String miner name
     * 
     * @param Integer initial difficulty of previous block
     */
    public Block(String previousHash, String miner, Integer difficulty) {
        this.previousHash = previousHash;
        this.miner = miner;
        this.difficulty = difficulty;
        mineBlock();
    }

    /*
     * Function that calculates the SHA-256 of the previousHash + miner + nonce
     * 
     * @return SHA-256 hash String
     */
    public String calculateHash() {
        // Joins the data together
        String data = previousHash + miner + nonce;
        MessageDigest x;
        try {
            // gets SHA-256
            x = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }

        // converts data to new SHA-256
        byte[] newHash = x.digest(data.getBytes(StandardCharsets.UTF_8));
        StringBuilder newString = new StringBuilder();
        for (byte b : newHash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1)
                newString.append('0');
            newString.append(hex);
        }

        // return SHA-256 Hash
        return newString.toString();
    }

    /*
     * Function that mines a new block
     */
    public void mineBlock() {
        boolean found = false;

        // finds a new random nonce, currently a hexadecimal string, was previously an
        // integer string
        Random rand = new Random();
        nonce = Long.toHexString(rand.nextLong());

        // calculates SHA-256 hash with new nonce
        hash = calculateHash();
        int count = 0;

        // Loops until a new block is found
        while (found == false) {

            // converts SHA-256 hash to binary
            String binaryStringWithZeros = convertString(hash);

            // removes leading 0's from new binary string
            String zeros = binaryStringWithZeros.replaceFirst("^0+", "");

            // checks if the number of leading 0's is greater than difficulty
            if (binaryStringWithZeros.length() - zeros.length() > this.difficulty) {
                // sets new difficulty of new found block
                this.difficulty = binaryStringWithZeros.length() - zeros.length();

                // Output data of new block mined
                System.out.println("Block mined: " + hash);
                System.out.println("Difficulty: " + this.difficulty);
                System.out.println("Nonce: " + nonce);
                System.out.println("Iterations needed: " + count);

                // sets found to true to end the loop
                found = true;
            } else {
                // gets new nonce and calculates new SHA-256 hash
                nonce = Long.toHexString(rand.nextLong());
                hash = calculateHash();
            }
            // increment new count
            count++;
        }
    }

    /*
     * Function that converts HEX SHA-256 String to binary string.
     * 
     * @param String hex string
     * 
     * @return String binary string
     */
    String convertString(String hex) {

        // sets strinb variable
        String binary = "";

        // creates a hashmap
        HashMap<Character, String> map = new HashMap<Character, String>();

        // sets values of hashmap
        map.put('0', "0000");
        map.put('1', "0001");
        map.put('2', "0010");
        map.put('3', "0011");
        map.put('4', "0100");
        map.put('5', "0101");
        map.put('6', "0110");
        map.put('7', "0111");
        map.put('8', "1000");
        map.put('9', "1001");
        map.put('a', "1010");
        map.put('b', "1011");
        map.put('c', "1100");
        map.put('d', "1101");
        map.put('e', "1110");
        map.put('f', "1111");

        // loops through hex string and converts each hex to binary
        for (int i = 0; i < hex.length(); i++) {
            char ch = hex.charAt(i);
            if (map.containsKey(ch))
                // adds to binary string
                binary += map.get(ch);
            else {
                // sets binary string to error message
                binary = "Invalid Hexadecimal String";
                return binary;
            }
        }
        // returns binary string
        return binary;
    }

}

// Class for the blockchain
class Blockchain {
    // Chain for the blocks
    private List<Block> chain;
    // current difficulty
    int difficulty;

    /*
     * Constructor for blockchain that sets variables
     * 
     * @param String miner
     * 
     * @param initial difficulty
     */
    public Blockchain(int difficulty) {
        this.chain = new ArrayList<>();
        this.difficulty = difficulty;
    }

    /*
     * Function that adds a block to the chain
     * 
     * @param Block to be added
     */
    public void addBlock(Block x) {
        chain.add(x);
    }

    // Main
    public static void main(String[] args) {
        try {
            // Opens a file writer on file
            FileWriter writer = new FileWriter("blocks.txt", true);

            // Set starting data for mining
            String miner = "SpicyChilliNuts";
            int difficulty = 20;
            String previousHash = "00000a2ed46cd277a0edc3f17ff3df541b034345f4696d75744279166e19d8eb";

            // Initialise a blockchain
            Blockchain blockchain = new Blockchain(difficulty);

            // Find first block and add it the the chain, along with setting new difficulty
            Block initialBlock = new Block(previousHash, miner, difficulty);
            blockchain.addBlock(initialBlock);
            blockchain.difficulty = initialBlock.difficulty;

            // write the first block to the text file
            Block y = blockchain.chain.get(0);
            writer.write(y.previousHash + miner + y.nonce);
            writer.write(System.getProperty("line.separator"));
            writer.write(y.hash);
            writer.close();

            int count = 1;

            // Blockchain loop until certain difficulty is reached
            while (blockchain.difficulty < 40) {
                // Finding next block, adding it the the chain and setting the new difficulty
                Block block = new Block(blockchain.chain.get(count - 1).hash, miner, blockchain.difficulty);
                blockchain.addBlock(block);
                blockchain.difficulty = block.difficulty;

                // Printing out the chain once a block is found
                System.out.println("--------------------------------------------------------------------------");
                System.out.println("Chain:");
                for (int x = 0; x < blockchain.chain.size(); x++) {
                    Block tempblock = blockchain.chain.get(x);
                    System.out.println("Previous Hash: " + tempblock.previousHash);
                    System.out.println("Nonce: " + tempblock.nonce);
                    System.out.println("Hash Created: " + tempblock.hash);
                    System.out.println();
                }
                System.out.println("--------------------------------------------------------------------------");

                // incrementing the counter
                count++;

                // writing the new block to the file
                Block temp = blockchain.chain.get(count - 1);
                FileWriter w = new FileWriter("blocks.txt", true);
                w.write(miner + temp.nonce);
                w.write(System.getProperty("line.separator"));
                w.write(temp.hash);
                w.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}