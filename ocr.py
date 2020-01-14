from PIL import Image
import pytesseract
im=Image.open("transformed.jpg")
text = pytesseract.image_to_string(im, lang= 'eng')
text=text.strip()
print(text)
