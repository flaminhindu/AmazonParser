import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Document;




public class Review{

	private File reviewPagePath;
	private Document reviewPage;

	private boolean gotFeatured;
	private String[] featuredID;

	private boolean[] mostFavorable;
	private boolean[] mostCritical;

	DateTimeFormatter formatter;

	private String[] ReviewID;
	private String[] ReviewTitle;
	private String[] ReviewerID;
	private String[] ReviewerName;
	private String[] helpfulVote;
	private LocalDate[] datePosted;
	private int[] Rating;
	private String[] location;
	private boolean[] verified;
	private boolean[] vine;
	private boolean[] hasComments;
	private int[] numberOfComments;
	private int[] contentWordCount;
	private String[] content;
	

	public Review(String absolutePath, boolean featured){

		featuredID = new String[2];
		mostFavorable = new boolean[10];
		mostCritical = new boolean[10];
		ReviewID =  new String[10];
		ReviewTitle =  new String[10];
		ReviewerID = new String[10];
		ReviewerName= new String[10];
		helpfulVote= new String[10];
		datePosted= new LocalDate[10];
		Rating= new int[10];
		location= new String[10];
		verified= new boolean[10];
		vine= new boolean[10];
		hasComments= new boolean[10];
		numberOfComments= new int[10];
		contentWordCount= new int[10];
		content= new String[10];


		gotFeatured = featured;

		reviewPagePath = new File(absolutePath);
		try {
			reviewPage = Jsoup.parse(reviewPagePath, "UTF-8");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//get the featured reviews from the first review page
		if (!gotFeatured){
			getFeaturedReviews();
			gotFeatured = true;
		}

		formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH);
		//extract information from review Page
		getReviewInformation();

	}

	private void getFeaturedReviews() {
		// TODO Auto-generated method stub
		Elements featuredContent = reviewPage.select("div[class=cBoxInner] table tbody tr td a");
		int index = 0;
		for (Element f : featuredContent){
			if (f.hasAttr("name")){
				featuredID[index] = f.attr("name");
				index++;
			}		
		}
	}

	private void getReviewInformation() {

		//get review IDs
		Elements reviewIDContent = reviewPage.select("table#productReviews tbody tr td a");
		int index = 0;
		for (Element h : reviewIDContent){
			if (h.hasAttr("name") && !h.attr("name").contains(".") && !h.attr("name").contains("-")){
				ReviewID[index] = h.attr("name");
				//System.out.println(ReviewID[index]);
				index++;
			}
		}

		//get review Title
		Elements titleContent = reviewPage.select("table#productReviews tbody tr span[style=vertical-align:middle;] b");
		index = 0;
		for (Element h : titleContent){
			ReviewTitle[index] = h.text();
			index++;
		}

		//get reviewer ID and reviewer Name
		Elements reviewerIDandNameContent = reviewPage.select("table#productReviews tbody tr a[href^=/gp/pdp/profile/]");
		index = 0;
		for (Element h : reviewerIDandNameContent){	
			ReviewerID[index] = h.attr("href").split("/")[4];
			ReviewerName[index] = h.text();
			index++;
		}

		//helpful Vote
		Elements helpfulVotesContent = reviewPage.select("table#productReviews tbody tr div[style=margin-left:0.5em;] div[style=margin-bottom:0.5em;]");
		index = 0;
		for (Element h : helpfulVotesContent){
			if (h.text().contains("people found the following review helpful")){
				helpfulVote[index] = h.text().split("people")[0].replace(" of ", "/");
				index++;
			}	
		}

		//review date
		Elements datesContent = reviewPage.select("table#productReviews tbody tr span[style=vertical-align:middle;] nobr");
		index = 0;
		for (Element h : datesContent){
			datePosted[index] = LocalDate.parse(h.text(), formatter);
			index++;
		}

		//get review Rating
		Elements ratingsContent = reviewPage.select("table#productReviews tbody tr span[style=margin-right:5px;]");
		index = 0;
		for (Element h : ratingsContent){
			Rating[index] = Integer.parseInt(h.text().substring(0, 1));
			index++;
		}


		//get review location
		Elements locationContent = reviewPage.select("table#productReviews tbody tr div[style=margin-bottom:0.5em;] div[style=float:left;]");
		index = 0;
		//filtering lots of unnecessary text
		for (Element h : locationContent){
			if (!h.text().contains("By")){
				if (h.text().contains("(")){
					String temp = h.text().replace(ReviewerName[index] + " ", "").replace("- See all my reviews", "").trim();
					if (temp.contains("(")){
						temp = temp.substring(temp.indexOf("(") + 1, temp.indexOf(")"));
						if (!temp.contains("REAL NAME") && !temp.contains("VINE VOICE")){
							location[index] = temp;
						}
						else{
							location[index] = "no location";
						}
					}
					else{
						location[index] = "no location";
					}
				}
				else{
					location[index] = "no location";
				}
				index++;
			}	
		}
		
		
		//verified
		Elements verifiedContent = reviewPage.select("table#productReviews tbody tr td div[style=margin-left:0.5em;]");
		index = 0;
		for (Element h : verifiedContent){
			if (h.text().contains("Verified Purchase")){
				verified[index] = true;
			}else{
				verified[index] = false;
			}
			index++;
		}
		
		//vine
		Elements vineContent = reviewPage.select("table#productReviews tbody tr div[style=margin-bottom:0.5em;] span[class=small] b");
		index = 0;
		for (Element h : vineContent){
			if (h.text().contains("Vine Customer Review of Free Product")){
				vine[index] = true;
			}else{
				vine[index] = false;
			}		
			index++;
		}
		
		//check if comments available
		Elements commentsContent = reviewPage.select("table#productReviews tbody tr td div[style=white-space:nowrap;padding-left:-5px;padding-top:5px;]");
		index = 0;
		for (Element h : commentsContent){
			String comment = h.select("a").get(1).text();
			if (comment.contains("(")){
				hasComments[index] = true;
				numberOfComments[index] = Integer.parseInt(comment.substring(comment.indexOf("(") + 1, comment.indexOf(")")));
			}
			else{
				numberOfComments[index] = 0;
				hasComments[index] = false;
			}
			//System.out.println( hasComments[index] + ":" + numberOfComments[index] );
			index++;
		}
		
		//review content
		Elements reviewContent = reviewPage.select("table#productReviews div[class=reviewText]");
		index = 0;
		for (Element h : reviewContent){
			contentWordCount[index] = h.text().split(" ").length;
			content[index] = h.text();
			index++;
		}

	}

	public boolean getFeatured() {
		return gotFeatured;
	}
	public String getFeaturedID(int i) {
		return featuredID[i];
	}
	
	public String getReviewID(int j) {
		return ReviewID[j];
	}
	public String getReviewTitle(int j) {
		return ReviewTitle[j];
	}
	public String getReviewerID(int j) {
		return ReviewerID[j];
	}
	public String getReviewerName(int j) {
		return ReviewerName[j];
	}
	public String getHelpfulVote(int j) {
		return helpfulVote[j];
	}
	public LocalDate getDate(int j) {
		return datePosted[j];
	}
	public int getRating(int j) {
		return Rating[j];
	}
	public void setMostFavorable(int j, boolean b) {
		mostFavorable[j] = b;
	}

	public void setMostCritical(int j, boolean b) {
		mostCritical[j] = b;
	}

	public boolean getMostFavorable(int j) {
		return mostFavorable[j]	;	
	}

	public boolean getMostCritical(int j) {
		return mostCritical[j];

	}
	public String getLocation(int j) {
		// TODO Auto-generated method stub
		return location[j];
	}

	public boolean getVerified(int j) {
		// TODO Auto-generated method stub
		return verified[j];
	}

	public boolean getVine(int j) {
		// TODO Auto-generated method stub
		return vine[j];
	}

	public boolean getHasComments(int j) {
		// TODO Auto-generated method stub
		return hasComments[j];
	}

	public int getNumberOfComments(int j) {
		// TODO Auto-generated method stub
		return numberOfComments[j];
	}

	public int getWordCount(int j) {
		// TODO Auto-generated method stub
		return contentWordCount[j];
	}

	public String getContent(int j) {
		// TODO Auto-generated method stub
		return content[j];
	}


}
