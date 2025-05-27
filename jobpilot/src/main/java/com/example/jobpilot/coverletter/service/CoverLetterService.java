package com.example.jobpilot.coverletter.service;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.jobpilot.ai.service.OpenAiService;
import com.example.jobpilot.coverletter.dto.CoverLetterDTO;
import com.example.jobpilot.coverletter.dto.CoverLetterRequest;
import com.example.jobpilot.coverletter.dto.CoverLetterResponse;
import com.example.jobpilot.coverletter.mapper.CoverLetterMapper;
import com.example.jobpilot.coverletter.model.CoverLetter;
import com.example.jobpilot.coverletter.repository.CoverLetterRepository;
import com.example.jobpilot.job.model.Job;
import com.example.jobpilot.job.repository.JobRepository;
import com.example.jobpilot.resume.model.Resume;
import com.example.jobpilot.resume.repository.ResumeRepository;
import com.example.jobpilot.user.model.User;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CoverLetterService {

    private final ResumeRepository resumeRepository;
    private final JobRepository jobRepository;
    private final OpenAiService openAiService;
    private final CoverLetterRepository coverLetterRepository;
    private final CoverLetterMapper coverLetterMapper;
    @Transactional
    public CoverLetterDTO generateCoverLetter(CoverLetterRequest request,User user) {
    Resume resume = resumeRepository.findById(request.getResumeId())
                .orElseThrow(() -> new RuntimeException("Resume not found"));

        Job job = jobRepository.findById(request.getJobId())
                .orElseThrow(() -> new RuntimeException("Job not found"));

        String coverLetterText = openAiService.generateCoverLetter(
                resume.getParsedSummary(),
                job.getTitle(),
                job.getCompany(),
                job.getDescription()
        );

        CoverLetter coverLetter = new CoverLetter();
        coverLetter.setUser(user); 
        coverLetter.setJob(job);
        coverLetter.setContent(coverLetterText);
        coverLetter.setCreatedAt(Instant.now());
        coverLetter.setCoverLetterName(job.getCompany() + " - " + job.getTitle());
        coverLetterRepository.save(coverLetter);
        CoverLetterDTO coverLetterDTO = coverLetterMapper.toDTO(coverLetter);
        return coverLetterDTO;
    }
    public List<CoverLetterDTO> getAllCoverLettersByUser(User user) {
        return coverLetterRepository.findAllByJobUser(user).stream()
                .map(coverLetterMapper::toDTO)
                .toList();
    }

    public CoverLetterResponse getCoverLetter(UUID jobId, User user) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        if (!job.getUser().getUserId().equals(user.getUserId())) {
            throw new RuntimeException("Unauthorized access to cover letter");
        }

        CoverLetter letter = coverLetterRepository.findByJob(job)
                .orElseThrow(() -> new RuntimeException("Cover letter not found"));

        return new CoverLetterResponse(letter.getContent());
    }

    @Transactional
    public CoverLetterDTO updateCoverLetter(UUID jobId, String newContent, User user) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));
        if (!job.getUser().getUserId().equals(user.getUserId())) {
            throw new RuntimeException("Unauthorized");
        }

        CoverLetter coverLetter = coverLetterRepository.findByJob(job)
                .orElseThrow(() -> new RuntimeException("Cover letter not found"));

        coverLetter.setContent(newContent);
        coverLetter.setCreatedAt(Instant.now());
        CoverLetter saved = coverLetterRepository.save(coverLetter);
        return coverLetterMapper.toDTO(saved);
    }

    public String improveCoverLetter(UUID jobId, String instruction, User user) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        if (!job.getUser().getUserId().equals(user.getUserId())) {
            throw new RuntimeException("Unauthorized");
        }

        CoverLetter letter = coverLetterRepository.findByJob(job)
                .orElseThrow(() -> new RuntimeException("Cover letter not found"));

        String improved = openAiService.improveText(letter.getContent(), instruction);
        letter.setContent(improved);
        coverLetterRepository.save(letter);
        return improved;
    }
    public CoverLetterDTO getCoverLetterByIdForUser(UUID coverLetterId, User user) {
    CoverLetter coverLetter = coverLetterRepository.findById(coverLetterId)
        .orElseThrow(() -> new RuntimeException("Cover letter not found"));

    if (!coverLetter.getJob().getUser().getUserId().equals(user.getUserId())) {
        throw new RuntimeException("Unauthorized access to this cover letter");
    }

    return coverLetterMapper.toDTO(coverLetter);
    }

    public void deleteCoverLetter(UUID coverLetterId, User user) {
        CoverLetter coverLetter = coverLetterRepository.findById(coverLetterId)
            .orElseThrow(() -> new RuntimeException("Cover Letter not found"));

        if (!coverLetter.getUser().getUserId().equals(user.getUserId())) {
            throw new RuntimeException("Access denied: You are not authorized to delete this cover letter.");
        }

        coverLetterRepository.delete(coverLetter);
    }

}
