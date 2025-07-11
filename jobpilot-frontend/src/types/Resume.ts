// src/types/Resume.ts
export type Resume = {
  id: string;
  filename: string;
  s3Url: string;
  atsScore:number;
  parsedName: string;
  parsedEmail: string;
  parsedPhone: string;
  parsedSkills: string[];
  parsedSummary: string;
  uploadedAt: string;
};
