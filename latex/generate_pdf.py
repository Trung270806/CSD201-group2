import fitz

html_path = "latex/report.html"
pdf_path = "latex/report.pdf"

with open(html_path, "r", encoding="utf-8") as f:
    html_content = f.read()

# A4 dimensions in points: 595.28 x 841.89
page_width = 595.28
page_height = 841.89
margin = 40.0
rect = fitz.Rect(margin, margin, page_width - margin, page_height - margin)

story = fitz.Story(html=html_content)
doc = fitz.open()

more = True
while more:
    page = doc.new_page(width=page_width, height=page_height)
    story.place(rect)
    more, _ = story.draw(page)

doc.save(pdf_path)
print(f"SUCCESS: {pdf_path} created successfully ({len(doc)} pages).")
