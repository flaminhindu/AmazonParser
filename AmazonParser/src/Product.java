
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class Product {

	
	private  String productFolderPath;
	private  File	productPagePath;

	private File reviewFolder;
	private File[] reviewFolderFiles;


	private PrintWriter productWriter;
	private PrintWriter reviewWriter;
	private  ArrayList<Review> reviewObjects = new ArrayList<Review>();

	private Document productPage;

	private  String title;
	private  String productASINID;
	private  String ProductUrl;
	private double productAverageRating;
	private int[] productStarRatings;

	private  int productReviewCount;

	private String[] helpfulVotesArray;
	private int[] helpfulRatingsArray;
	private String[] helpfulAuthorName;
	private String[] helpfulDateWritten;
	private String[] helpfulReviews;
	private String[] helpfulReviewIDs;
	private String[] helpfulReviewerIDs;
	private Boolean[] helpfulverified;

	private int[] recentRatingsArray;
	private String[] recentReviewIDArray;
	private String[] recentAuthorNameArray;
	private String[] recentReviewArray;
	private File dateFolder;
	private File[] reviewFiles;
	private String productBrandName;
	private boolean[] hasComments;
	private int[] numberOfComments;
	private String[] commentsURL;
	private BigDecimal listPrice;
	private BigDecimal currentPrice;
	private BigDecimal priceSaved;
	private BigDecimal percentSaved;
	private int majorRankNumber;
	private String majorRankCategory;
	private String itemName;
	private LocalDate dateFirstAvailable;

	private LocalDate dateCollected;
	private String productCategory;
	
	private String[] helpfulTitle;
	private Boolean[] helpfulVine;
	private boolean gotFeatured = false;
	private String mosthelpfulFavorableID;
	private String mosthelpfulCriticalID;


	public Product(String filePath, String dName, String cName, String pName) throws ParseException, FileNotFoundException, UnsupportedEncodingException{

		productFolderPath = filePath;

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMddyy", Locale.ENGLISH);
		dateCollected = LocalDate.parse(dName, formatter);
		productCategory = pName;

		//get the product ID for this product from the folder name, note some pages have a different ASIN then the folder names, but they both point to the same product
		productASINID = pName;
		//filepath for the product html page in the product folder
		productPagePath = new File(productFolderPath + "\\" +  productASINID + ".html");

		System.out.println(productFolderPath);
		System.out.println(productASINID);
		System.out.println(productPagePath);



		//Parse the product html page using jsoup.

			try {
				productPage = Jsoup.parse(productPagePath, "UTF-8");
			} catch (IOException e) {
				e.printStackTrace();
			}


		//extract information from product Page
		getProductInformation();

		//output product information
		outputProductInformation();


		
		//parse the review pages
		String reviewFolderPath = productFolderPath + "\\reviews";

		reviewFolder = new File(reviewFolderPath);
		reviewFolderFiles = reviewFolder.listFiles();
		sortFilesNumerically(reviewFolderFiles);
		System.out.println("review folder path: " + reviewFolderPath);
		for (File r : reviewFolderFiles){			
			reviewWriter = new PrintWriter("Output\\" + productASINID + "_" + dateCollected + "_ReviewPage.txt", "UTF-8");
			reviewWriter.println("ReviewID" + "||" +  "ReviewTitle" + "||" +  "ReviewerID" + "||" +  
								 "ReviewerName" + "||" +  "HelpfulVotes" + "||" +  "DatePosted" + "||" +
								 "Rating"  + "||" + "MostHelpfulFavorable" + "||" + "MostHelpfulCritical"  + "||" + "Location" + "||" + "Verified"  + "||" + "Vine" + "||" + 
								 "CommentsAvailable" + "||" +"#OfComments" + "||" + "WordCount"+"||"+ "Content"+ "||");

			if (r.isFile()){
				reviewObjects.add(new Review(r.getAbsolutePath(), gotFeatured));
				if (reviewObjects.size() == 1){
					gotFeatured = reviewObjects.get(0).getFeatured();
					mosthelpfulFavorableID = reviewObjects.get(0).getFeaturedID(0);
					mosthelpfulCriticalID = reviewObjects.get(0).getFeaturedID(1);
					System.out.println(mosthelpfulFavorableID +":"+mosthelpfulCriticalID);
				}
			}
		
			int i = 0;
			while (i <  reviewObjects.size()){
				int j = 0;
				while (j < 10){
					//check if most helpful favorable or most critical review is seen in the entire list of reviews
					if ((reviewObjects.get(i).getReviewID(j) != null) && (mosthelpfulFavorableID != null) && (mosthelpfulCriticalID != null)){
						
						if (mosthelpfulFavorableID.contentEquals(reviewObjects.get(i).getReviewID(j))){
							reviewObjects.get(i).setMostFavorable(j, true);
						}
						else{
							reviewObjects.get(i).setMostFavorable(j, false);
						}
						if (mosthelpfulCriticalID.contentEquals(reviewObjects.get(i).getReviewID(j))){
							reviewObjects.get(i).setMostCritical(j, true);
						}
						else{
							reviewObjects.get(i).setMostCritical(j, false);
						}
						
					}
					reviewWriter.println(
							reviewObjects.get(i).getReviewID(j) + "||" +
							reviewObjects.get(i).getReviewTitle(j) + "||" +
							reviewObjects.get(i).getReviewerID(j) + "||" +
							reviewObjects.get(i).getReviewerName(j) + "||" +
							reviewObjects.get(i).getHelpfulVote(j) + "||" +
							reviewObjects.get(i).getDate(j) + "||" +
							reviewObjects.get(i).getRating(j) + "||" +
							reviewObjects.get(i).getMostFavorable(j) + "||" +
							reviewObjects.get(i).getMostCritical(j) + "||" +
							reviewObjects.get(i).getLocation(j) + "||" +
							reviewObjects.get(i).getVerified(j) + "||" +
							reviewObjects.get(i).getVine(j) + "||" +
							reviewObjects.get(i).getHasComments(j) + "||" +
							reviewObjects.get(i).getNumberOfComments(j) + "||" +
							reviewObjects.get(i).getWordCount(j) + "||" +
							reviewObjects.get(i).getContent(j) + "||"
							);
					
					j++;
				}
				i++;
			}
			reviewWriter.flush();
			reviewWriter.close();
		}
	}


	private void sortFilesNumerically(File [] reviewFiles) {
		Arrays.sort(reviewFiles, new Comparator<File>()
				{
			@Override
			public int compare(File f1, File f2) {
				int fileId1 = Integer.parseInt(f1.getName().split("_")[2].split("\\.")[0]);
				int fileId2 = Integer.parseInt(f2.getName().split("_")[2].split("\\.")[0]);

				return fileId1 - fileId2;
			}
				});

	}


	private void getProductInformation(){

		//productUrl created with productID
		ProductUrl = "http://www.amazon.com/dp/" + productASINID;
		//title of page
		title = productPage.title();
		//item name
		itemName = productPage.select("div#centerCol h1").text();
		//number of reviews for this product
		productReviewCount = Integer.parseInt(productPage.select("div#averageCustomerReviews").text().split(" ")[0].trim().replace(",", ""));
		//average product rating
		productAverageRating = Double.parseDouble(productPage.select("span#acrPopover").attr("title").toString().split("out")[0].trim());
		//get the company who are selling the product
		productBrandName = productPage.select("a#brand").text();
		//get date product was first made available if it exists
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH);
		if (productPage.select("tr.date-first-available td").hasText()){
			dateFirstAvailable = LocalDate.parse(productPage.select("tr.date-first-available td").get(1).text(), formatter);
		}
		
		BigDecimal hundred = new BigDecimal(100);
		//get list price
		if (1 < productPage.select("div#price_feature_div tr td").size()){
			listPrice = BigDecimal.valueOf(Double.parseDouble(productPage.select("div#price_feature_div tr td").get(1).text().replace("$", "").trim().split(" ")[0]));
		}
		else{
			listPrice= BigDecimal.valueOf(0);
		}
		//get current price
		if (1 < productPage.select("div#price_feature_div tr").size()){
			currentPrice = BigDecimal.valueOf(Double.parseDouble(productPage.select("div#price_feature_div tr").get(1).select("td").get(1).text().split(" ")[0].replace("$", "")));
		}
		else{
			currentPrice= listPrice;
		}
		
		//get price saved
		priceSaved = listPrice.subtract(currentPrice);
		percentSaved = hundred.subtract(currentPrice.divide(listPrice, 2, BigDecimal.ROUND_HALF_UP).multiply(hundred));

		//get main rank number and associated rank category
		//get ranking information from product information
		if (productPage.select("tr#SalesRank td").hasText()){
			majorRankNumber = Integer.parseInt(productPage.select("tr#SalesRank td").get(1).text().split("in")[0].replace("#","").trim());
			majorRankCategory = productPage.select("tr#SalesRank td").get(1).text().split("in")[1].split("\\(")[0].trim();
		}else{
			//get ranking information from product details
			if (productPage.select("li#SalesRank").hasText()){
				majorRankNumber = Integer.parseInt(productPage.select("li#SalesRank").text().split("[(]")[0].split(":")[1].trim().split(" ")[0].replace("#", ""));
				majorRankCategory = productPage.select("li#SalesRank").text().split("[(]")[0].split(":")[1].split(" in ")[1].trim();
				System.out.println(majorRankNumber + ":" + majorRankCategory);
			}
		}
		
/*
		System.out.println("title: " + title);
		System.out.println("item name: " + itemName);
		System.out.println("Date first available: " + dateFirstAvailable);
		System.out.println("Date Collected: " + dateCollected);
		System.out.println("ID: " + productASINID);
		System.out.println("total Reviews: " + productReviewCount);
		System.out.println("productURL: " + ProductUrl);
		System.out.println("product Average Rating: " + productAverageRating);
		System.out.println("product Seller: " + productBrandName);
		System.out.println("list Price: " + listPrice);
		System.out.println("current Price: " + currentPrice);
		System.out.println("price saved: " + priceSaved);
		System.out.println("percent saved: " + percentSaved);
		System.out.println("major rank number: " + majorRankNumber);
		System.out.println("major rank category: " + majorRankCategory);

*/


		//get the different star ratings
		productStarRatings = new int[5];
		int index = 0;
		Elements productStarRatingsContent = productPage.select("table#histogramTable tr");
		for (Element r : productStarRatingsContent){
			productStarRatings[index] = Integer.parseInt(r.text().split("star")[1].trim().replace(",", ""));
			//System.out.println((5 - index) + " star ratings: " + productStarRatings[index]);
			index++;
		}

		//get the ratings for the helpful reviews
		Elements helpfulRatingsContent = productPage.select("div#revMHRL a[class=a-link-normal a-text-normal a-color-base]");
		helpfulRatingsArray = new int[10];
		index = 0;
		for (Element h : helpfulRatingsContent){
			if (h.attr("title").length() > 0){
				helpfulRatingsArray[index] = Integer.parseInt(h.attr("title").substring(0, 1));
				index++;
			}
		}

		hasComments = new boolean[10];
		numberOfComments = new int[10];
		commentsURL = new String[10];

		//get the number of comments if any for each comment
		Elements commentsOnReviewContent = productPage.select("div#revMHRL a[class*=a-link-normal comment-link]");
		index = 0;
		for (Element h : commentsOnReviewContent){
			//review contains a comment
			if (!h.attr("title").contains("0")){
				numberOfComments[index] = Integer.parseInt(h.attr("title").toString());
				hasComments[index] = true;
				commentsURL[index] = h.attr("href").toString();
				//System.out.println("Comment Rating: " + helpfulRatingsArray[index] + ", " + "Comment Count for Review " + (index + 1) + ": " + numberOfComments[index] + ", Url: " + commentsURL[index]);
			}
			//review contains no comments
			else{
				hasComments[index] = false;
				//System.out.println("Comment Rating: " + helpfulRatingsArray[index] + ", " + "Review " + (index + 1) + " has no comment");
			}

			index++;
		}
		//get the helpful reviews title
		Elements helpfulTitleContent = productPage.select("div#revMHRL a[class=a-link-normal a-text-normal a-color-base] span");
		helpfulTitle = new String[10];
		index = 0;
		for (Element h : helpfulTitleContent){
			helpfulTitle[index] =  h.text();
			//System.out.println("Title:" + helpfulTitle[index]);
			index++;
		}
		
		
		//get the helpful votes for the helpful reviews
		Elements helpfulVoteContent = productPage.select("div#revMHRL span[class=a-size-small a-color-secondary]");
		helpfulVotesArray = new String[10];
		index = 0;
		for (Element h : helpfulVoteContent){
			if (!h.text().contains("says")){
				helpfulVotesArray[index] =  h.text().split("people")[0].replace(" of ", "/").trim();
				//System.out.println("helpful vote:" + helpfulVotesArray[index]);
				index++;
			}

		}

		
		//get the author name 
		Elements helpfulAuthorNameContent = productPage.select("div#revMHRL span[class=a-color-secondary]");
		helpfulAuthorName = new String[10];
		index = 0;
		for (Element h : helpfulAuthorNameContent){
			if (h.select("a").hasText()){
				helpfulAuthorName[index] = h.select("a").text();
				//System.out.println("author: " + helpfulAuthorName[index]);
				index++;
			}
		}
		//get the date review was posted
		Elements helpfulDateContent = productPage.select("div#revMHRL span[class=a-color-secondary]");
		helpfulDateWritten = new String[10];
		index = 0;
		for (Element h : helpfulDateContent){
			if (h.select("span").text().contains("on") && !h.select("span").text().contains("By")){
				helpfulDateWritten[index] = h.select("span[class=a-color-secondary]").text().replace("on ", "").trim();
				//System.out.println("date: " + helpfulDateWritten[index]);
				index++;
			}
		}
		
		//get the review id from the helpful reviews
		Elements helpfulReviewIDsContent = productPage.select("div#revMHRL  div[id^=rev-dpReviews");
		helpfulReviewIDs = new String[10];
		index = 0;
		for (Element h : helpfulReviewIDsContent){
			helpfulReviewIDs[index] = (h.attr("id").split("-")[2]);
			//System.out.println("reviewID: " + helpfulReviewIDs[index]);
			index++;
		}
		
		//get the reviewer id
		Elements helpfulReviewerIDContent = productPage.select("div#revMHRL span[class=a-color-secondary] a");
		helpfulReviewerIDs = new String[10];
		index = 0;
		for (Element h : helpfulReviewerIDContent){
			if (!h.attr("href").split("/")[4].contains("help")){
				helpfulReviewerIDs[index] = h.attr("href").split("/")[4];
				//System.out.println("reviewer id: " + helpfulReviewerIDs[index]);
				index++;
			}

		}
		

		
		//get the review content from the helpful reviews
		Elements helpfulReviewContent = productPage.select("div#revMHRL div[class=a-section]");
		helpfulReviews = new String[10];
		index = 0;
		for (Element h : helpfulReviewContent){
			if (!h.text().contains("The manufacturer commented on this review")){
				helpfulReviews[index] = (h.text());
				//System.out.println("content:" + helpfulReviews[index]);
				index++;
			}

		}

		//get the verified stripe from the helpful reviews
		Elements helpfulVerifiedContent = productPage.select("div#revMHRL  div[id^=revData-dpReviews");
		helpfulverified = new Boolean[10];
		index = 0;
		for (Element h : helpfulVerifiedContent){
			if (h.text().contains(" Verified Purchase ")){
				helpfulverified[index] = true;
			}
			else{
				helpfulverified[index] = false;
			}
			//System.out.println("verified:" + helpfulverified[index]);
			index++;
		}


		//get the vine stripe from the helpful reviews
		Elements helpfulVineContent = productPage.select("div#revMHRL  div[id^=revData-dp]");
		helpfulVine = new Boolean[10];
		index = 0;
		for (Element h : helpfulVineContent){
			if (h.text().contains("Vine")){
				helpfulVine[index] = true;
			}
			else{
				helpfulVine[index] = false;
			}
			//System.out.println("Vine: " + helpfulVine[index]);
			index++;
		}
		
		
		//extracting recent reviews information
		recentRatingsArray = new int[10];
		recentReviewIDArray = new String[10];
		recentAuthorNameArray = new String[10];
		recentReviewArray = new String[10];

		//get the rating from the helpful reviews
		Elements recentRatings = productPage.select("div#revMRRL  a[class=a-link-normal a-text-normal]");
		index = 0;
		for (Element h : recentRatings){
			if (h.attr("title").contains("stars")){
				recentRatingsArray[index] = Integer.parseInt(h.attr("title").substring(0, 1));
				index++;
			}

		}

		//get the reviewID from recent reviews
		Elements recentReviewIDs = productPage.select("div#revMRRL  a[class=a-link-normal a-text-normal a-color-base]");
		index = 0;
		for (Element h : recentReviewIDs){
			recentReviewIDArray[index] = h.attr("href").split("#")[1];
			index++;
		}


		//get the author name and review snippet from recent reviews
		Elements recentAuthorNames = productPage.select("div#revMRRL  a[class=a-link-normal a-text-normal]");
		index = 0;
		for (Element h : recentAuthorNames){
			if (h.text().contains("Published")){
				recentAuthorNameArray[index] = h.text().split("Published")[1].split("by")[1].trim();
				recentReviewArray[index] =h.text().split("Published")[0];
				index++;
			}
		}
	}

	private void outputProductInformation() throws FileNotFoundException, UnsupportedEncodingException {

		productWriter = new PrintWriter("Output\\" + productASINID  + "_" + dateCollected + "_ProductPage.txt", "UTF-8");

		//write the product information to the specified file.
		productWriter.println("ItemName"  + "||" +  "BrandName" + "||" + "ProductID" + "||" + "DatePageCollected" + "||" + 
								"DateFirstAvailable" + "||" + "MajorRank#" +   "||" + "MajorRankCategory" + "||" + "ListPrice" + "||" + "CurrentPrice" + "||" + "PriceSaved" + "||"+ "PercentSaved" + "||"  +"Product_Rating" + "||" + "5_Star" + "||" + "4_Star" + "||" + 
									"3_Star" + "||"  + "2_Star" + "||"  + "1_Star" + "||"  + "Product_Url"  + "||" + 
										"Review_Count" + "||");
		productWriter.println(itemName + "||" +  productBrandName + "||"  + productASINID + "||" + dateCollected + "||" + 
										dateFirstAvailable + "||" +  majorRankNumber  + "||" + majorRankCategory  + "||"  + listPrice + "||" + currentPrice + "||" + priceSaved + "||"+ percentSaved + "||"  + productAverageRating + "||" + productStarRatings[0] + "||" + productStarRatings[1] + "||" + 
											productStarRatings[2] + "||"  + productStarRatings[3] + "||"  + productStarRatings[4] + "||"  + ProductUrl  + "||" + 
												productReviewCount + "||");
		productWriter.println("Most Helpful Reviews"  + "||");
		productWriter.println("Review_ID"+ "||"+"Review_Title"+"||" + "||"+"Reviewer_ID"+"||" +"Reviewer_Name"+"||" + "Date"+"||"+"Rating"+ "||"+"Helpful_Vote"+ "||"+"Purchase_Verified" +"||"+"Vine_Customer" +"||"+"Comments_Available" +"||"+"#ofComments" +"||"+"CommentsUrl"  + "||" +"Content"+"||");

		//print out the elements for most helpful reviews
		for (int i = 0; i < 10; i++){
			productWriter.println(	helpfulReviewIDs[i] + "||" +
					helpfulTitle[i] + "||" +
					helpfulReviewerIDs[i] + "||" +
					helpfulAuthorName[i]+ "||" +
					helpfulDateWritten[i]	+ "||" +		
					helpfulRatingsArray[i]+ "||" +
					helpfulVotesArray[i]+ "||" +
					helpfulverified[i]+ "||" +
					helpfulVine[i] + "||" +
					hasComments[i] + "||" +
					numberOfComments[i] + "||" +
					commentsURL[i] + "||" +
					helpfulReviews[i]+ "||");
		}

		productWriter.println("Most_Recent_Reviews"  + "||");
		productWriter.println("Review_ID"+"||"+"Rating"+"||"+"Reviewer_Name"+"||"+"Review_Snippet" + "||");

		for (int i = 0; i < 10; i++){
			productWriter.println(	recentReviewIDArray[i] + "||" +
					recentRatingsArray[i]+ "||" +
					recentAuthorNameArray[i]	+ "||"+
					recentReviewArray[i]	+ "||");
		}
		productWriter.flush();
		productWriter.close();

	}


	public String getProductID() {
		return productASINID;
	}


	private void setProductID(String productID) {
		this.productASINID = productID;
	}


	public Document getProductPage() {
		return productPage;
	}


	public static void setProductPage(Document productPage) {
		productPage = productPage;
	}


	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}



}
