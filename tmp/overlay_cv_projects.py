from io import BytesIO
from pathlib import Path

from pypdf import PdfReader, PdfWriter
from reportlab.lib import colors
from reportlab.lib.styles import ParagraphStyle
from reportlab.pdfbase import pdfmetrics
from reportlab.pdfbase.ttfonts import TTFont
from reportlab.pdfgen import canvas
from reportlab.platypus import Paragraph


ROOT = Path(__file__).resolve().parents[1]
SOURCE = Path.home() / "Desktop" / "CV" / "EN" / "JoaoRochaCV.pdf"
OUTPUT = ROOT / "docs" / "output" / "pdf" / "JoaoRochaCV_LurePilotAI_edited.pdf"


def build_overlay(width, height):
    pdfmetrics.registerFont(TTFont("Arial", r"C:\Windows\Fonts\arial.ttf"))
    pdfmetrics.registerFont(TTFont("Arial-Bold", r"C:\Windows\Fonts\arialbd.ttf"))

    left = 213.0
    right = width - 25.0
    top = 292.0
    buffer = BytesIO()
    c = canvas.Canvas(buffer, pagesize=(width, height))

    # Cover only the original project block. All content above and the sidebar
    # remain in the original PDF page and are merged back unchanged.
    c.setFillColor(colors.white)
    c.rect(left - 4, 0, width - left + 4, top + 5, stroke=0, fill=1)

    c.setStrokeColor(colors.black)
    c.setLineWidth(0.8)
    c.line(left, top, right, top)
    c.setFillColor(colors.black)
    c.setFont("Arial-Bold", 11.5)
    c.drawString(left, top - 14.5, "RELEVANT PROJECTS")

    body = ParagraphStyle(
        "body",
        fontName="Arial",
        fontSize=8.35,
        leading=10.5,
        textColor=colors.HexColor("#171717"),
        spaceAfter=0,
    )
    compact = ParagraphStyle(
        "compact",
        fontName="Arial",
        fontSize=7.95,
        leading=9.65,
        textColor=colors.HexColor("#171717"),
        spaceAfter=0,
    )

    y = top - 33

    def draw_paragraph(text, style, gap=4):
        nonlocal y
        p = Paragraph(text, style)
        _, h = p.wrap(right - left, height)
        p.drawOn(c, left, y - h)
        y -= h + gap

    draw_paragraph(
        "- <b>LurePilot AI:</b> Local-first AI fishing copilot built with Java 21, Spring Boot, PostgreSQL, React Native Web, Docker and LM Studio.",
        body,
        gap=2,
    )
    draw_paragraph(
        "Integrates structured fishing context, Open-Meteo weather and Solunar data to generate validated A/B/C strategies and session adjustments.",
        compact,
        gap=2,
    )
    draw_paragraph(
        "Includes spots, fish and lure libraries, Lure Box, sessions, catch-photo gallery, recommendation history and analytics; the complete flow was validated locally with a real LLM.",
        compact,
        gap=5,
    )
    draw_paragraph(
        "- <b>MalsTRUST:</b> Complaint management app for athletes and clubs (Flutter, Spring Boot, SQL Server).",
        compact,
        gap=2,
    )
    draw_paragraph(
        "- <b>Clube Desportivo:</b> Full-stack website with online store, ticketing and admin panel (Laravel, MySQL).",
        compact,
        gap=2,
    )
    draw_paragraph(
        "- <b>Data Analysis:</b> Integrated and analysed multiple datasets with Python, Pandas and visualisations.",
        compact,
        gap=2,
    )
    draw_paragraph(
        "- <b>Distributed System:</b> File sharing and synchronisation system using Java RMI and RabbitMQ.",
        compact,
        gap=0,
    )

    c.showPage()
    c.save()
    buffer.seek(0)
    return PdfReader(buffer).pages[0]


def main():
    source_reader = PdfReader(str(SOURCE))
    page = source_reader.pages[0]
    width = float(page.mediabox.width)
    height = float(page.mediabox.height)
    page.merge_page(build_overlay(width, height))

    OUTPUT.parent.mkdir(parents=True, exist_ok=True)
    writer = PdfWriter()
    writer.add_page(page)
    writer.add_metadata({
        "/Title": "Joao Rocha CV - LurePilot AI",
        "/Author": "Joao Rocha",
        "/Subject": "Backend Java Developer CV",
    })
    with OUTPUT.open("wb") as file:
        writer.write(file)
    print(OUTPUT)


if __name__ == "__main__":
    main()
