export const MEDIAS = ['MS','MM','ML','TP','TL','DS','DM','DL'] as const;
export type Media = typeof MEDIAS[number];