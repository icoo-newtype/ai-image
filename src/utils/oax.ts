import AXIOS, { AxiosRequestConfig } from 'axios';
import qs from 'qs';
import { getCookie } from './index';
import { assign, forEach, groupBy, mapValues, omit, pickBy, some, trim } from 'lodash-es';

const axios = AXIOS.create({
  baseURL: import.meta.env.VITE_API_HOST, //localhost:8080
  withCredentials: true
});

axios.interceptors.request.use((request) => {
  if (!request.params) request.params = {};

  // const copyLang = new URLSearchParams(request.data).get('copyLang');
  // request.params.lang = copyLang || getCookie('admin-lang') || 'ko';
  if (request.method === 'get') {
    request.params._ = Date.now();
  }
  return request;
});
const trimOrPass = (row: any) => (row && typeof row === 'string' ? trim(row) : row);
const prep = (data?: Record<string, any>) => pickBy(mapValues(omit(data, ['files', 'lang']), trimOrPass));

const get = <T>(url: string, data?: Record<string, any>, config?: AxiosRequestConfig) => {
  const params = prep(data);
  return axios.get<T>(url, assign(config, { params }));
};

const del = <T>(url: string, data?: Record<string, any>, config?: AxiosRequestConfig) => {
  const params = prep(data);
  return axios.delete<T>(url, assign(config, { params }));
};

const put = <T>(url: string, data: Record<string, any> | string, config: AxiosRequestConfig) => {
  let nData;
  if (typeof data === 'string') nData = data;
  else {
    const params = prep(data);
    nData = qs.stringify(params);
  }
  return axios.put<T>(url, nData, config);
};

const $post = <T>(url: string, params: Record<string, any>, config?: AxiosRequestConfig) => {
  const nData = qs.stringify(params, { indices: false });
  return axios.post<T>(url, nData, config);
};

const $multipart = <T>(url: string, params: Record<string, any>, config?: AxiosRequestConfig) => {
  assign(config, { headers: { 'content-type': 'multipart/form-data' } });
  const formData = new FormData();
  forEach(params, (item, key) => {
    if (`${item}` === 'FILE_INPUT_MODEL') {
      forEach(item.add, (file) => {
        formData.append(key, file);
      });
      forEach(item.remove, (remove) => {
        formData.append('removeFiles', remove);
      });
      forEach(item.exist, (exist) => {
        formData.append('existFiles', exist.sq);
      });
    } else {
      formData.append(key, item);
    }
  });
  return axios.post<T>(url, formData, config);
};

const post = <T>(url: string, data: Record<string, any> | string, config?: AxiosRequestConfig) => {
  if (typeof data === 'string') return axios.post<T>(url, data, config);
  if (Array.isArray(data)) return axios.post<T>(url, data, config);
  const params = prep(data);
  if (some(params, row => `${row}` === 'FILE_INPUT_MODEL')) return $multipart<T>(url, params, config);
  return $post<T>(url, params, config);
};

const postJson = <T>(url: string, data: Record<string, any> | string) => {
  return axios.post<T>(url, data, { headers: { 'Content-Type': 'application/json' } });
};

const multipart = <T>(url: string, data: Record<string, any>, config?: AxiosRequestConfig) => {
  const params = prep(data);
  return $multipart<T>(url, params, config);
};

const fillFile = (info: Record<string, any>) => {
  forEach(groupBy(info.files, 'name'), (files, name) => {
    info[name] = { toString: () => 'FILE_INPUT_MODEL', exist: files };
  });
};

const setAuthToken = (authToken: string) => {
  axios.defaults.headers.common['X-AUTH-TOKEN'] = authToken;
};

export default {
  get,
  post,
  postJson,
  multipart,
  put,
  delete: del,
  fillFile,
  setAuthToken,
  stringify: qs.stringify,
};
