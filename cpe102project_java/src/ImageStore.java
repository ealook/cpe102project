import processing.core.*;
import processing.core.PConstants;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ImageStore {

    private static final String DEFAULT_IMAGE_NAME = "background_default";
    private static final int COLOR_MASK = 0xffffff;
    private String src_path;
    private String image_list_file_name;
    private Map<String, ArrayList<PImage>> images = new HashMap();

    PApplet parent;

    public ImageStore(Main main, String src_path, String image_list_file_name) {
        this.parent = main;
        this.src_path = src_path;
        this.image_list_file_name = image_list_file_name;
    }

    public Map<String, ArrayList<PImage>> get_images() {
        return images;
    }

    public String getDefaultImageName() {
        return DEFAULT_IMAGE_NAME;
    }

    private PImage create_default_image(int tile_width, int tile_height) {
        return parent.loadImage(src_path + "none.bmp");
    }

    public void load_images(int tile_width, int tile_height) throws IOException {

        File file = new File(src_path + image_list_file_name);
        BufferedReader br = new BufferedReader(new FileReader(file));

        String line = null;

        while ((line = br.readLine()) != null) {
            process_image_line(line);
        }

        br.close();

        if (!images.containsKey(DEFAULT_IMAGE_NAME)) {
            PImage default_img = create_default_image(tile_width, tile_height);
            ArrayList<PImage> default_list = new ArrayList<PImage>();
            default_list.add(default_img);
            images.put(DEFAULT_IMAGE_NAME, default_list);
        }
    }

    private void process_image_line(String line) {
        String[] attrs = line.split(" ");

        if (attrs.length >= 2) {
            String key = attrs[0];

            // Create the image and remove masks if needed.
            PImage img = setAlpha(parent.loadImage(src_path + attrs[1]), parent.color(252, 252, 252), 0);
            img = setAlpha(img, parent.color(201, 26, 26), 0);
            if (key.compareTo("blob") == 0 || key.compareTo("quake") == 0
                    || key.compareTo("spongebob") == 0 || key.compareTo("gary") == 0
                    || key.compareTo("krabbypatty") == 0 || key.compareTo("patrick") == 0
                    || key.compareTo("sandy") == 0 || key.compareTo("krabs") == 0
                    || key.compareTo("pineapple_bottomleft") == 0 || key.compareTo("pineapple_bottomright") == 0
                    || key.compareTo("pineapple_middleleft") == 0 || key.compareTo("pineapple_middleright") == 0
                    || key.compareTo("pineapple_topleft") == 0 || key.compareTo("pineapple_topright") == 0) {
                img = setAlpha(img, parent.color(255, 255, 255), 0);
            }

            if (img != null) {
                ArrayList<PImage> imgs = get_images_internal(key);
                imgs.add(img);
                images.put(key, imgs);
            }
        }
    }

    private static PImage setAlpha(PImage img, int maskColor, int alpha) {
        maskColor &= COLOR_MASK;
        img.format = PConstants.ARGB;
        img.loadPixels();
        for (int i = 0; i < img.pixels.length; i++)
        {
            if ((img.pixels[i] & COLOR_MASK) == maskColor)
            {
                img.pixels[i] = (alpha << 24) | maskColor;
            }
        }
        img.updatePixels();
        return img;
    }

    private ArrayList<PImage> get_images_internal(String key) {
        if (images.containsKey(key)) {
            return images.get(key);
        } else {
            return new ArrayList<PImage>();
        }
    }

    public ArrayList<PImage> get_images(String key) {
        if (images.containsKey(key)) {
            return images.get(key);
        } else {
            return images.get(DEFAULT_IMAGE_NAME);
        }
    }
}