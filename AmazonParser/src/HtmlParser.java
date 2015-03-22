
import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;


public class HtmlParser {

	private static ArrayList<Product> productObjects = new ArrayList<Product>();
	private static String productFolder;

	/**
	 * Parser to extract Product and Review related information from locally stored Amazon Web Pages
	 * @param args
	 * @author Vishnu Govindaraj
	 */
	public static void main(String[] args) {
		//get the product folder info from run-time arguments, use default name if none provided
		if (args.length == 1){
			productFolder = args[0];
			System.out.println("Using product folder name: " + productFolder);
		}
		else{
			System.out.println("Unable to use product name, using default name: products");
			productFolder = "products";
		}
		startParse();

	}

	private static void startParse() {

		//delete output folder if it exists
		File currentFolder = new File(System.getProperty("user.dir"));
		File[] currentFolderFiles = currentFolder.listFiles();
		for (File c : currentFolderFiles){
			if (c.isDirectory()){
				if (c.getName().equals("Output")){
					File outputFolder = new File(c.getAbsolutePath());
					File[] outputFiles = outputFolder.listFiles();
					for (File o : outputFiles){
						o.delete();
					}
					c.delete();
				}
			}
		}
		
		
		//make new output folder
		File output = new File("Output");
		if (output.mkdir()){
			System.out.println("Outputfolder created at " + output.getAbsolutePath());
		}

		File datesFolder = new File(productFolder);
		File[] listOfDates = datesFolder.listFiles();

		//for every date
		for (File date : listOfDates){
			if (date.isDirectory()){
				File categoryFolder = new File(date.getAbsolutePath());
				File[] listOfCategories = categoryFolder.listFiles();
				//for every category
				for (File category : listOfCategories){
					if (category.isDirectory()){
						File productFolder = new File(category.getAbsolutePath());
						File[] listOfProducts = productFolder.listFiles();
						//for every product
						for (File product : listOfProducts){
							if (product.isDirectory()){
								//System.out.println(date.getName() +":" +category.getName() + ":" + product.getName());
								try {
									productObjects.add(new Product(product.getPath(), date.getName(),category.getName(),product.getName()));

								} catch (FileNotFoundException | UnsupportedEncodingException
										| ParseException e) {
									e.printStackTrace();
								}
							}
							else{
								System.out.println("ERROR! " + product.getName() + " is a file, product folder expected");
							}

						}
					}
					else{
						System.out.println("ERROR! " + category.getName() + " is a file, category folder expected");
					}
				}
			}
			else{
				System.out.println("ERROR! " + date.getName() + " is a file, date folders with the format 'mmddyy' expected");
			}
		}	
	}



}
