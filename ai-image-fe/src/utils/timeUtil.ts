import dayjs from 'dayjs';

export const formDate = (v: string | null) => v ? dayjs(v).format('YYYY.MM.DD') : '-';
export const formDateTime = (v: string | null) => v ? dayjs(v).format('YYYY.MM.DD HH:mm') : '-';