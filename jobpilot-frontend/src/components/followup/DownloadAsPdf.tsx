import { jsPDF } from 'jspdf';

export const downloadAsPdf = (subject: string, body: string) => {
  const doc = new jsPDF();

  // Set subject as title
  doc.setFontSize(16);
  doc.setFont('helvetica', 'bold');
  doc.text(subject, 10, 20);

  // Set body text
  doc.setFontSize(12);
  doc.setFont('helvetica', 'normal');

  // Split long body into lines
  const lines = doc.splitTextToSize(body, 180); // 180 = line width in mm
  doc.text(lines, 10, 35);

  // Save
  const safeTitle = subject.replace(/[^\w\s]/gi, '').substring(0, 30);
  doc.save(`${safeTitle || 'follow-up-email'}.pdf`);
};
