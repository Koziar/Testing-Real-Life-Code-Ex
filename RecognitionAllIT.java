
package net.sf.javaanpr.test;

import net.sf.javaanpr.imageanalysis.CarSnapshot;
import net.sf.javaanpr.intelligence.Intelligence;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;

/**
 * Created by Lukasz Koziarski (cph-lk139) on 07/03/2017.
 */
@RunWith(Parameterized.class)
public class RecognitionAllIT {
    // instance variables for parametrized test
    // number-plate snapshot
    File currentImage;
    // expected number-plate
    String expectedPlate;

    public RecognitionAllIT(File currentImage, String expectedPlate) {
        this.currentImage = currentImage;
        this.expectedPlate = expectedPlate;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testDataCreator() throws Exception {
        // path to the snapshots (image names)
        final String SNAPSHOTS = "src/test/resources/snapshots";
        // path to the results file
        final String RESULTS = "src/test/resources/results.properties";

        // open the results as an input stream
        InputStream resultsStream = new FileInputStream(new File(RESULTS));
        Properties properties = new Properties();
        properties.load(resultsStream);
        resultsStream.close();
        // make sure the load succeeded
        assertTrue(properties.size() > 0);

        // load the snapshots into a File array
        File snapshotDir = new File(SNAPSHOTS);
        File[] snapshots = snapshotDir.listFiles();
        assertNotNull(snapshots);
        // make sure the load succeeded
        assertTrue(snapshots.length > 0);

        // returns the collection (as array) of objects with the corresponding comparison values
        Collection<Object[]> dataForOneImage = new ArrayList();
        for (File file : snapshots) {
            String name = file.getName();
            String plateExpected = properties.getProperty(name);
            dataForOneImage.add(new Object[]{file, plateExpected});
        }
        return dataForOneImage;
    }

    @Test
    public void testAllSnapshots() throws Exception {
        CarSnapshot carSnapshot = new CarSnapshot(new FileInputStream(currentImage));

        Intelligence intelligence = new Intelligence();
        String numberPlate = intelligence.recognize(carSnapshot, false);

        assertThat(expectedPlate, equalTo(numberPlate));
    }
}
