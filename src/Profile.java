import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/*Class for loading the property file*/
public class Profile {

	Properties properties;

	public Profile() {
		properties = new Properties();
		try {
			properties.load(new FileReader("participants.properties"));
		} catch (IOException e) {
		}
	}
}
