from pathlib import Path

from PIL import Image
from reportlab.lib import colors
from reportlab.lib.enums import TA_LEFT
from reportlab.lib.pagesizes import A4
from reportlab.lib.styles import ParagraphStyle
from reportlab.lib.units import mm
from reportlab.pdfbase import pdfmetrics
from reportlab.pdfbase.ttfonts import TTFont
from reportlab.pdfgen import canvas
from reportlab.platypus import Paragraph


ROOT = Path(__file__).resolve().parents[1]
SOURCE_RENDER = ROOT / "tmp" / "pdfs" / "original" / "cv-1.png"
PHOTO_PATH = ROOT / "tmp" / "cv_photo.jpg"
OUTPUT = ROOT / "docs" / "output" / "pdf" / "JoaoRochaCV_LurePilotAI.pdf"


def crop_photo():
    image = Image.open(SOURCE_RENDER).convert("RGB")
    # Crop the portrait from the original one-page CV render.
    image.crop((52, 34, 383, 368)).save(PHOTO_PATH, quality=94)


def register_fonts():
    pdfmetrics.registerFont(TTFont("Segoe", r"C:\Windows\Fonts\segoeui.ttf"))
    pdfmetrics.registerFont(TTFont("Segoe-Bold", r"C:\Windows\Fonts\segoeuib.ttf"))


PAGE_W, PAGE_H = A4
SIDEBAR_W = 174
MAIN_X = SIDEBAR_W + 26
MAIN_W = PAGE_W - MAIN_X - 22

NAVY = colors.HexColor("#153D46")
TEAL = colors.HexColor("#0A8792")
INK = colors.HexColor("#152D35")
MUTED = colors.HexColor("#53666C")
LIGHT = colors.HexColor("#F3F8F8")
PALE_BLUE = colors.HexColor("#E8F3F4")
SIDEBAR_TEXT = colors.HexColor("#F5FBFB")


def style(name, size, leading=None, color=INK, bold=False, space_after=0):
    return ParagraphStyle(
        name=name,
        fontName="Segoe-Bold" if bold else "Segoe",
        fontSize=size,
        leading=leading or size * 1.25,
        textColor=color,
        alignment=TA_LEFT,
        spaceAfter=space_after,
    )


BODY = style("Body", 8.25, 10.5)
BODY_SMALL = style("BodySmall", 7.6, 9.3)
BODY_MUTED = style("BodyMuted", 8.1, 10.2, MUTED)
SIDEBAR_BODY = style("SidebarBody", 8.1, 10.3, SIDEBAR_TEXT)
SIDEBAR_SMALL = style("SidebarSmall", 7.4, 9.2, SIDEBAR_TEXT)


def paragraph(c, text, x, y_top, width, pstyle):
    p = Paragraph(text, pstyle)
    _, height = p.wrap(width, PAGE_H)
    p.drawOn(c, x, y_top - height)
    return y_top - height


def section_heading(c, title, x, y_top, width):
    c.setStrokeColor(TEAL)
    c.setLineWidth(1.2)
    c.line(x, y_top, x + width, y_top)
    c.setFillColor(TEAL)
    c.setFont("Segoe-Bold", 9.4)
    c.drawString(x, y_top - 15, title)
    return y_top - 28


def bullet(c, text, x, y_top, width, pstyle=BODY):
    c.setFillColor(TEAL)
    c.circle(x + 2.5, y_top - 5.5, 1.7, stroke=0, fill=1)
    return paragraph(c, text, x + 10, y_top, width - 10, pstyle) - 3


def draw_sidebar(c):
    c.setFillColor(NAVY)
    c.rect(0, 0, SIDEBAR_W, PAGE_H, stroke=0, fill=1)

    c.drawImage(str(PHOTO_PATH), 25, PAGE_H - 166, width=124, height=125, preserveAspectRatio=True, mask="auto")
    c.setStrokeColor(colors.HexColor("#BBD6D8"))
    c.setLineWidth(0.8)
    c.line(25, PAGE_H - 180, SIDEBAR_W - 24, PAGE_H - 180)

    y = PAGE_H - 198
    c.setFillColor(SIDEBAR_TEXT)
    c.setFont("Segoe-Bold", 10.8)
    c.drawString(25, y, "CONTACT")
    y -= 19
    for line in [
        "Penafiel, Porto",
        "+351 961 456 473",
        "jpr13.rocha@gmail.com",
        "linkedin.com/in/joao-rocha-79839a33b/",
    ]:
        y = paragraph(c, line, 25, y, SIDEBAR_W - 49, SIDEBAR_SMALL) - 6

    y -= 5
    c.setStrokeColor(colors.HexColor("#BBD6D8"))
    c.line(25, y, SIDEBAR_W - 24, y)
    y -= 17
    c.setFillColor(SIDEBAR_TEXT)
    c.setFont("Segoe-Bold", 10.8)
    c.drawString(25, y, "CORE SKILLS")
    y -= 18
    skills = [
        "<b>Backend:</b> Java, Spring Boot, JPA/Hibernate, REST APIs, JSON, WebSockets",
        "<b>Databases:</b> PostgreSQL, SQL Server",
        "<b>Frontend:</b> React Native Web, React, Flutter, HTML, CSS, JavaScript",
        "<b>DevOps &amp; Tools:</b> Git, GitHub, Docker, IntelliJ, VS Code, PyCharm",
        "<b>Other:</b> Python, C, PHP, Arduino, OpenGL",
    ]
    for item in skills:
        y = bullet(c, item, 25, y, SIDEBAR_W - 50, SIDEBAR_SMALL)

    y -= 8
    c.setStrokeColor(colors.HexColor("#BBD6D8"))
    c.line(25, y, SIDEBAR_W - 24, y)
    y -= 17
    c.setFillColor(SIDEBAR_TEXT)
    c.setFont("Segoe-Bold", 10.8)
    c.drawString(25, y, "LANGUAGES")
    y -= 18
    y = paragraph(c, "<b>Portuguese:</b> First language", 25, y, SIDEBAR_W - 50, SIDEBAR_SMALL) - 6
    y = paragraph(c, "<b>English:</b> C2 / Proficient", 25, y, SIDEBAR_W - 50, SIDEBAR_SMALL) - 10
    c.setFillColor(colors.HexColor("#BBD6D8"))
    c.setFont("Segoe", 6.9)
    c.drawString(25, 22, "github.com/JoaoRocha13/lurepilot-ai")


def draw_main(c):
    right = MAIN_X + MAIN_W
    y = PAGE_H - 30

    c.setFillColor(INK)
    c.setFont("Segoe-Bold", 27)
    c.drawString(MAIN_X, y, "João Rocha")
    y -= 23
    c.setFillColor(TEAL)
    c.setFont("Segoe-Bold", 10.2)
    c.drawString(MAIN_X, y, "BACKEND JAVA DEVELOPER  |  FULL-STACK & LOCAL AI APPLICATIONS")
    y -= 13
    c.setStrokeColor(INK)
    c.setLineWidth(1.4)
    c.line(MAIN_X, y, right, y)
    y -= 21

    y = section_heading(c, "SUMMARY", MAIN_X, y, MAIN_W)
    y = paragraph(
        c,
        "Backend Java Developer with a degree in Computer Engineering and professional experience in Java, Spring Boot, SQL databases and REST APIs. Interested in backend development, distributed systems and practical AI products, with a focus on clean architecture, reliable integrations and end-to-end ownership.",
        MAIN_X,
        y,
        MAIN_W,
        style("Summary", 8.8, 11.2),
    ) - 15

    y = section_heading(c, "FEATURED PERSONAL PROJECT", MAIN_X, y, MAIN_W)
    box_top = y + 6
    box_bottom = y - 139
    c.setFillColor(PALE_BLUE)
    c.roundRect(MAIN_X - 7, box_bottom, MAIN_W + 14, box_top - box_bottom, 7, stroke=0, fill=1)
    c.setFillColor(INK)
    c.setFont("Segoe-Bold", 11.5)
    c.drawString(MAIN_X, y - 4, "LurePilot AI  |  Local AI Fishing Copilot")
    y -= 17
    c.setFillColor(TEAL)
    c.setFont("Segoe-Bold", 7.6)
    c.drawString(MAIN_X, y, "JAVA 21  |  SPRING BOOT  |  POSTGRESQL  |  REACT NATIVE WEB  |  LM STUDIO  |  DOCKER")
    y -= 13
    project_bullets = [
        "Built a local-first full-stack copilot that turns fishing spot, species, lure, weather and Solunar context into practical A/B/C strategies.",
        "Integrated LM Studio (<b>qwen2.5-7b-instruct</b>) through an OpenAI-compatible API; the backend prepares structured context, validates model output against available lures and stores confidence/warnings.",
        "Delivered the complete product loop: plans, sessions, catch-photo gallery, fish/lure libraries, Lure Box, Open-Meteo weather, Solunar forecasts, session adjustments, recommendation history and outcome analytics.",
        "Applied layered MVC, DTOs, JPA/Flyway, Docker Compose and local operations with PowerShell startup/backup scripts and Tailscale iPhone access. End-to-end flow validated locally with a real LM Studio model.",
    ]
    for item in project_bullets:
        y = bullet(c, item, MAIN_X, y, MAIN_W - 3, BODY_SMALL)
    y = box_bottom - 15

    y = section_heading(c, "PROFESSIONAL EXPERIENCE", MAIN_X, y, MAIN_W)
    c.setFillColor(INK)
    c.setFont("Segoe-Bold", 9.3)
    c.drawString(MAIN_X, y, "BACKEND JAVA DEVELOPER")
    c.setFillColor(MUTED)
    c.setFont("Segoe", 8.0)
    c.drawRightString(right, y, "11/2025 - Current")
    y -= 13
    c.setFillColor(TEAL)
    c.setFont("Segoe-Bold", 8.3)
    c.drawString(MAIN_X, y, "EGITRON  |  MOZELOS, PORTUGAL")
    y -= 14
    for item in [
        "Develop and maintain backend features using Java, Spring Boot and SQL Server.",
        "Work with REST APIs, database queries and enterprise application logic.",
        "Debug, test and improve existing functionality while collaborating through Git and agile workflows.",
    ]:
        y = bullet(c, item, MAIN_X, y, MAIN_W, BODY_SMALL)
    y -= 8

    y = section_heading(c, "EDUCATION", MAIN_X, y, MAIN_W)
    y = paragraph(c, "<b>Bachelor's:</b> Computer Engineering, 09/2021 - 07/2025<br/><b>Universidade Fernando Pessoa</b> - Porto", MAIN_X, y, MAIN_W, BODY_SMALL) - 12

    y = section_heading(c, "CERTIFICATIONS", MAIN_X, y, MAIN_W)
    y = paragraph(c, "<b>Cambridge English:</b> C1 Advanced Certificate<br/><b>University of Helsinki:</b> Java Programming I &amp; II (MOOC)", MAIN_X, y, MAIN_W, BODY_SMALL) - 12

    y = section_heading(c, "SELECTED ACADEMIC PROJECTS", MAIN_X, y, MAIN_W)
    for item in [
        "<b>MalsTRUST:</b> Complaint management app for athletes and clubs (Flutter, Spring Boot, SQL Server).",
        "<b>Clube Desportivo:</b> Full-stack website with online store, ticketing and admin panel (Laravel, MySQL).",
        "<b>Data Analysis:</b> Integrated and analysed multiple datasets with Python, Pandas and visualisations.",
        "<b>Distributed System:</b> File sharing and synchronisation system using Java RMI and RabbitMQ.",
    ]:
        y = bullet(c, item, MAIN_X, y, MAIN_W, BODY_SMALL)


def build():
    crop_photo()
    register_fonts()
    OUTPUT.parent.mkdir(parents=True, exist_ok=True)
    c = canvas.Canvas(str(OUTPUT), pagesize=A4)
    c.setTitle("Joao Rocha CV - LurePilot AI")
    c.setAuthor("Joao Rocha")
    c.setSubject("Backend Java Developer CV")
    draw_sidebar(c)
    draw_main(c)
    c.showPage()
    c.save()
    print(OUTPUT)


if __name__ == "__main__":
    build()
