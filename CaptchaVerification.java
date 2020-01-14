import java.io.*;
import java.awt.image.BufferedImage; 
import javax.imageio.ImageIO;

class CaptchaVerification{
    final int limit=5; 
    final int halfLimit=(int)Math.sqrt((limit*limit)/2);
    private BufferedImage takingTheImageInput(){
        try{
            File file = new File("captcha.jpg");
            BufferedImage bufferedImage = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_ARGB); 
            bufferedImage = ImageIO.read(file);
            return bufferedImage;
        }
        catch(Exception exception){
            System.out.println("Cannot input the image. Exception: "+exception);
            System.exit(0);
            return null;
        }
    }
    private void threasholdTheImage(BufferedImage bufferedImage){
        for(int i=0;i<bufferedImage.getWidth();i++){
            for(int j=0;j<bufferedImage.getHeight();j++){
                int color = bufferedImage.getRGB(i, j);
                int blue = color & 0xff;
                int green = (color & 0xff00) >> 8;
                int red = (color & 0xff0000) >> 16;
                if(red<100 && blue<100 && green<100){
                    red=0;
                    blue=0;
                    green=0;
                    int rgb = (red << 16 | green << 8 | blue);
                    bufferedImage.setRGB(i, j, rgb);
                }
            }
        }
    }
    private void swappingColors(BufferedImage bufferedImage){
        for(int i=0;i<bufferedImage.getWidth();i++){
            for(int j=0;j<bufferedImage.getHeight();j++){
                int color = bufferedImage.getRGB(i, j);
                int blue = color & 0xff;
                int green = (color & 0xff00) >> 8;
                int red = (color & 0xff0000) >> 16;
                if(red>180 && blue>180 && green>180){
                    red=0;
                    blue=0;
                    green=0;
                }
                else{
                    red=255;
                    blue=255;
                    green=255;
                }
                int rgb = (red << 16 | green << 8 | blue);
                bufferedImage.setRGB(i, j, rgb);
            }
        }
    }
    private void modifingTheBlackishLine(BufferedImage bufferedImage){
        //Height is rows
        //Width is column
        //Background is 122 122 122
        int timesRepeat=1;
	while(timesRepeat-->=0){
		for(int i=0;i<bufferedImage.getWidth();i++){
		    for(int j=0;j<bufferedImage.getHeight();j++){
		        int color = bufferedImage.getRGB(i, j);
		        int blue = color & 0xff;
		        int green = (color & 0xff00) >> 8;
		        int red = (color & 0xff0000) >> 16;
		        if(red <100 && blue < 100 && green < 100){
		            if(searchColor(bufferedImage, i, j)){
		                red=255;
		                blue=255;
		                green=255;
		            }
		            else{
		                red=0;
		                blue=0;
		                green=0;
		            }
		            int rgb = (red << 16 | green << 8 | blue);
		            bufferedImage.setRGB(i, j, rgb);
		        }
		    }
		}
	}
        threasholdTheImage(bufferedImage);
        swappingColors(bufferedImage);
        writeTheModifiedImage(bufferedImage);
    }
    private void writeTheModifiedImage(BufferedImage bufferedImage){
        File ImageFile = new File("transformed.jpg");
        try {
            ImageIO.write(bufferedImage, "jpg", ImageFile);
        } catch (IOException e) {
            System.out.println("Cannot write the image.");
        }
    }
    private boolean searchColor(BufferedImage bufferedImage, int row, int column){
        //1 means white
        //-1 means black
        int top=topSearch(bufferedImage, row, column);
        int bottom=bottomSearch(bufferedImage, row, column);
        int start=startSearch(bufferedImage, row, column);
        int end=endSearch(bufferedImage, row, column);
        int leftTop=leftTopSearch(bufferedImage, row, column);
        int rightTop=rightTopSearch(bufferedImage, row, column);
        int leftBottom=leftBottomSearch(bufferedImage, row, column);
        int rightBottom=rightBottomSearch(bufferedImage, row, column);
        if(top+bottom+start+end+leftTop+rightTop+leftBottom+rightBottom > 3)
            return true;
        //true means a letter and hence white color
        //black means not a letter and hence black color
        return false;
    }
    private int topSearch(BufferedImage bufferedImage, int row, int column){
        int copy=row;
        int color=bufferedImage.getRGB(row, column);
        int blue = color & 0xff;
        int green = (color & 0xff00) >> 8;
        int red = (color & 0xff0000) >> 16;
        try{
            while(row>0 && red==0 && blue==0 && green==0){
                row--;
                color=bufferedImage.getRGB(row, column);
                blue = color & 0xff;
                green = (color & 0xff00) >> 8;
                red = (color & 0xff0000) >> 16;
            }
        }
        catch(Exception e){System.out.println("Coordinate in TOPSEARCH: "+row+" "+column);}
        if(red>240 && green>240 && blue>240 && copy-row<=limit)
            return 1;
        return 0;
    }
    private int bottomSearch(BufferedImage bufferedImage, int row, int column){
        int copy=row;
        int color=bufferedImage.getRGB(row, column);
        int blue = color & 0xff;
        int green = (color & 0xff00) >> 8;
        int red = (color & 0xff0000) >> 16;
        try{
            while(row<bufferedImage.getWidth()-1 && red==0 && blue==0 && green==0){
                row++;
                color=bufferedImage.getRGB(row, column);
                blue = color & 0xff;
                green = (color & 0xff00) >> 8;
                red = (color & 0xff0000) >> 16;
            }
        }
        catch(Exception e){System.out.println("Coordinate in BOTTOMSEARCH: "+row+" "+column);}
        if(red>240 && green>240 && blue>240 && row-copy<=limit)
            return 1;
        return 0;
    }
    private int startSearch(BufferedImage bufferedImage, int row, int column){
        int copy=column;
        int color=bufferedImage.getRGB(row, column);
        int blue = color & 0xff;
        int green = (color & 0xff00) >> 8;
        int red = (color & 0xff0000) >> 16;
        try{
            while(column>0 && red==0 && blue==0 && green==0){
                column--;
                color=bufferedImage.getRGB(row, column);
                blue = color & 0xff;
                green = (color & 0xff00) >> 8;
                red = (color & 0xff0000) >> 16;
            }
        }
        catch(Exception e){System.out.println("Coordinate in STARTSEARCH: "+row+" "+column);}
        if(red>240 && green>240 && blue>240 && copy-column<=limit)
            return 1;
        return 0;
    }
    private int endSearch(BufferedImage bufferedImage, int row, int column){
        int copy=column;
        int color=bufferedImage.getRGB(row, column);
        int blue = color & 0xff;
        int green = (color & 0xff00) >> 8;
        int red = (color & 0xff0000) >> 16;
        try{
            while(column<bufferedImage.getHeight()-1 && red==0 && blue==0 && green==0){
                column++;
                color=bufferedImage.getRGB(row, column);
                blue = color & 0xff;
                green = (color & 0xff00) >> 8;
                red = (color & 0xff0000) >> 16;
            }
        }
        catch(Exception e){System.out.println("Coordinate in ENDSEARCH: "+row+" "+column);}
        if(red>240 && green>240 && blue>240 && column-copy<=limit)
            return 1;
        return 0;
    }
    private int leftTopSearch(BufferedImage bufferedImage, int row, int column){
        int copyRow=row;
        int copyColumn=column;
        int color=bufferedImage.getRGB(row, column);
        int blue = color & 0xff;
        int green = (color & 0xff00) >> 8;
        int red = (color & 0xff0000) >> 16;
        try{
            while(row>0 && column>0 && red==0 && blue==0 && green==0){
                row--;
                column--;
                color=bufferedImage.getRGB(row, column);
                blue = color & 0xff;
                green = (color & 0xff00) >> 8;
                red = (color & 0xff0000) >> 16;
            }
        }
        catch(Exception e){System.out.println("Coordinate in LEFTTOPSEARCH: "+row+" "+column);}
        if(red>240 && green>240 && blue>240 && copyRow-row<=halfLimit && copyColumn-column<=halfLimit)
            return 1;
        return 0;
    }
    private int rightTopSearch(BufferedImage bufferedImage, int row, int column){
        int copyRow=row;
        int copyColumn=column;
        int color=bufferedImage.getRGB(row, column);
        int blue = color & 0xff;
        int green = (color & 0xff00) >> 8;
        int red = (color & 0xff0000) >> 16;
        try{
            while(row<bufferedImage.getWidth()-1 && column>0 && red==0 && blue==0 && green==0){
                row++;
                column--;
                color=bufferedImage.getRGB(row, column);
                blue = color & 0xff;
                green = (color & 0xff00) >> 8;
                red = (color & 0xff0000) >> 16;
            }
        }
        catch(Exception e){System.out.println("Coordinate in RIGHTTOPSEARCH: "+row+" "+column);}
        if(red>240 && green>240 && blue>240 && row-copyRow<=halfLimit && copyColumn-column<=halfLimit)
            return 1;
        return 0;
    }
    private int leftBottomSearch(BufferedImage bufferedImage, int row, int column){
        int copyRow=row;
        int copyColumn=column;
        int color=bufferedImage.getRGB(row, column);
        int blue = color & 0xff;
        int green = (color & 0xff00) >> 8;
        int red = (color & 0xff0000) >> 16;
        try{
            while(row>0 && column<bufferedImage.getHeight()-1 && red==0 && blue==0 && green==0){
                row--;
                column++;
                color=bufferedImage.getRGB(row, column);
                blue = color & 0xff;
                green = (color & 0xff00) >> 8;
                red = (color & 0xff0000) >> 16;
            }
        }
        catch(Exception e){System.out.println("Coordinate in LEFTBOTTOMSEARCH: "+row+" "+column);}
        if(red>240 && green>240 && blue>240 && copyRow-row<=halfLimit && column-copyColumn<=halfLimit)
            return 1;
        return 0;
    }
    private int rightBottomSearch(BufferedImage bufferedImage, int row, int column){
        int copyRow=row;
        int copyColumn=column;
        int color=bufferedImage.getRGB(row, column);
        int blue = color & 0xff;
        int green = (color & 0xff00) >> 8;
        int red = (color & 0xff0000) >> 16;
        try{
            while(row<bufferedImage.getWidth()-1 && column<bufferedImage.getHeight()-1 && red==0 && blue==0 && green==0){
                row++;
                column++;
                color=bufferedImage.getRGB(row, column);
                blue = color & 0xff;
                green = (color & 0xff00) >> 8;
                red = (color & 0xff0000) >> 16;
            }
        }
        catch(Exception e){System.out.println("Coordinate in RIGHTBOTTOMSEARCH: "+row+" "+column);}
        if(red>240 && green>240 && blue>240 && row-copyRow<=halfLimit && column-copyColumn<=halfLimit)
            return 1;
        return 0;
    }
    public static void startMakingABetterImage(){
        CaptchaVerification captchaVerification = new CaptchaVerification();
        BufferedImage bufferedImage=captchaVerification.takingTheImageInput();
        captchaVerification.modifingTheBlackishLine(bufferedImage);
    }
}
