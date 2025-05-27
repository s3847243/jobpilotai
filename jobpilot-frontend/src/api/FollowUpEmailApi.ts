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
  console.log(res);
  return res.data;
};

export const getAllFollowUpsForUser = async (): Promise<FollowUpEmail[]> => {
  const response = await axiosInstance.get<FollowUpEmail[]>('/follow-up/all');
  console.log(response.data);
  return response.data;
};

export const deleteFollowUpEmailById = async (followUpId: string) => {
  const response = await axiosInstance.delete(`/follow-up/${followUpId}`);
  return response.data;
};
