import core.Result;
import core.SlimJpg;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class OptimizerTest {

    private static final String OUT_DIRECTORY = "out/images/";

    @Test
    public void testSimCards() throws IOException {
        test("antiviaje-sim-cards.jpg", 591590, 586350);
    }

    @Test
    public void testQuitNow() throws IOException {
        test("download-quitnow.jpg", 31805, 31805);
    }

    private void test(String picture, long expectedOptimizedSizeWithMetadata, long expectedOptimizedSizeWithoutMetadata) throws IOException {
        System.out.println("- Original file: " + picture);

        byte[] original = new BinaryFileReader().load(picture);
        SlimJpg slimWithoutMetadata = new SlimJpg(original);
        Result optimizedWithoutMetadata = slimWithoutMetadata.optimize(1, 0, false);
        SlimJpg slimWithMetadata = new SlimJpg(original);
        Result optimizedWithMetadata = slimWithMetadata.optimize(1, 0, true);

        System.out.println("Size: " + printSizeInMb(original.length));
        System.out.println("\n- Optimization removing metadata");
        System.out.println("Size: " + printSizeInMb(optimizedWithoutMetadata.getPicture().length));
        System.out.println("Saved size: " + printSizeInMb(optimizedWithoutMetadata.getSavedBytes()));
        System.out.println("Saved ratio: " + optimizedWithoutMetadata.getSavedRatio() + "%");
        System.out.println("JPEG quality used: " + optimizedWithoutMetadata.getJpegQualityUsed() + "%");
        System.out.println("Time: " + optimizedWithoutMetadata.getElapsedTime() + "ms");
        System.out.println("\n- Optimization keeping metadata");
        System.out.println("Size: " + printSizeInMb(optimizedWithMetadata.getPicture().length));
        System.out.println("Saved size: " + printSizeInMb(optimizedWithMetadata.getSavedBytes()));
        System.out.println("Saved ratio: " + optimizedWithMetadata.getSavedRatio() + "%");
        System.out.println("JPEG quality used: " + optimizedWithoutMetadata.getJpegQualityUsed() + "%");
        System.out.println("Time: " + optimizedWithoutMetadata.getElapsedTime() + "ms");

        File directory = new File(OUT_DIRECTORY);
        directory.mkdirs();
        BinaryFileWriter writer = new BinaryFileWriter();
        writer.write(original, OUT_DIRECTORY + "original_" + picture);
        writer.write(optimizedWithoutMetadata.getPicture(), OUT_DIRECTORY + "optimized_wom_" + picture);
        writer.write(optimizedWithMetadata.getPicture(), OUT_DIRECTORY + "optimized_wm_" + picture);

        assertEquals(expectedOptimizedSizeWithMetadata, optimizedWithMetadata.getPicture().length);
        assertEquals(expectedOptimizedSizeWithoutMetadata, optimizedWithoutMetadata.getPicture().length);

        assertTrue(original.length > optimizedWithoutMetadata.getPicture().length);
        assertTrue(original.length > optimizedWithMetadata.getPicture().length);
        assertTrue(optimizedWithMetadata.getPicture().length >= optimizedWithoutMetadata.getPicture().length);
    }

    private String printSizeInMb(long length) {
        return length / 1024 + " KB";
    }
}