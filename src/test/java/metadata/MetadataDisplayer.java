package metadata;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import javax.imageio.metadata.IIOMetadata;

/**
 * This class is almost a copy of http://johnbokma.com/java/obtaining-image-metadata.html,
 * that is based in https://docs.oracle.com/javase/1.5.0/docs/guide/imageio/spec/apps.fm5.html
 * <p>
 * Thanks for it, John! And well, good job, Oracle. Tell Sun guys I said hi.
 */
public class MetadataDisplayer {

    public void displayMetadata(String title, IIOMetadata metadata) {
        System.out.println("\n-----------");
        System.out.println(title.toUpperCase());
        System.out.println("-----------\n");

        String[] names = metadata.getMetadataFormatNames();
        for (String name : names) {
            System.out.println("Format name: " + name);
            displayMetadata(metadata.getAsTree(name));
        }
    }

    private void displayMetadata(Node root) {
        displayMetadata(root, 0);
    }

    private void displayMetadata(Node node, int level) {
        indent(level); // emit open tag
        System.out.print("<" + node.getNodeName());
        NamedNodeMap map = node.getAttributes();
        if (map != null) { // print attribute values
            int length = map.getLength();
            for (int i = 0; i < length; i++) {
                Node attr = map.item(i);
                System.out.print(" " + attr.getNodeName() +
                        "=\"" + attr.getNodeValue() + "\"");
            }
        }

        Node child = node.getFirstChild();
        if (child != null) {
            System.out.println(">"); // close current tag
            while (child != null) { // emit child tags recursively
                displayMetadata(child, level + 1);
                child = child.getNextSibling();
            }
            indent(level); // emit close tag
            System.out.println("</" + node.getNodeName() + ">");
        } else {
            System.out.println("/>");
        }
    }

    private void indent(int level) {
        for (int i = 0; i < level; i++) {
            System.out.print("  ");
        }
    }
}
