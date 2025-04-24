import os
import tempfile
from parser import parse_pdf

from fastapi import FastAPI, File, UploadFile

app = FastAPI()


@app.post("/parse")
async def parse_resume(file: UploadFile = File(...)):
    with tempfile.NamedTemporaryFile(delete=False, suffix=".pdf") as tmp:
        tmp.write(await file.read())
        tmp_path = tmp.name

    try:
        parsed_data = parse_pdf(tmp_path)
        return parsed_data
    finally:
        os.remove(tmp_path)
