export interface JobSummaryDTO {
  id: string;              
  title: string;
  company: string;
  matchScore: number | null;
  status: string;          
  url: string;
}
