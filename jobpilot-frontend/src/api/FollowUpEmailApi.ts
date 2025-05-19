// followUpApi.ts

import { FollowUpEmail } from '../types/FollowUpEmail'
import axiosInstance from './axiosInstance';

export const getFollowUpByFollowUpId = async (followUpId: string): Promise<FollowUpEmail> => {
  const res = await axiosInstance.get(`/follow-up/${followUpId}`);
  return res.data;
};

export const generateFollowUpEmail = async (jobId: string): Promise<FollowUpEmail> => {
  const res = await axiosInstance.post(`/follow-up/generate/${jobId}`);
  console.log(res.data);
  return res.data;
};

export const improveFollowUpEmail = async (
  followUpId: string,
  instructions: string
): Promise<FollowUpEmail> => {
  const res = await axiosInstance.put(`/follow-up/${followUpId}/improve`, {
    instructions,
  });
  return res.data;
};

export const getAllFollowUpsForUser = async (): Promise<FollowUpEmail[]> => {
  const response = await axiosInstance.get<FollowUpEmail[]>('/follow-up/all');
  return response.data;
};
