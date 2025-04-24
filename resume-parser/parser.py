import spacy
from pdfminer.high_level import extract_text
from spacy.matcher import PhraseMatcher

nlp = spacy.load("en_core_web_sm")


def load_skills(filepath="skills.txt"):
    with open(filepath, "r", encoding="utf-8") as f:
        return [line.strip().lower() for line in f.readlines() if line.strip()]


skill_list = load_skills()
matcher = PhraseMatcher(nlp.vocab)
skill_patterns = [nlp(skill) for skill in skill_list]
matcher.add("SKILL", skill_patterns)


def parse_resume_text(text):
    doc = nlp(text)
    name = None
    email = None
    phone = None
    skills = set()

    # Basic personal info detection
    for ent in doc.ents:
        if ent.label_ == "PERSON" and not name:
            name = ent.text

    for token in doc:
        if token.like_email and not email:
            email = token.text
        if token.like_num and len(token.text) >= 10 and not phone:
            phone = token.text

    matches = matcher(doc)
    for match_id, start, end in matches:
        skills.add(doc[start:end].text.lower())

    return {
        "name": name,
        "email": email,
        "phone": phone,
        "skills": list(skills),
        "summary": text[:500]
    }


def parse_pdf(file_path: str):
    text = extract_text(file_path)
    return parse_resume_text(text)
