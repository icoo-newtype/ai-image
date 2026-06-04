type Role = 'ADMIN' | 'MANAGER';

type MainItem = {
  kvPc: string;
  kvMobile: string;
  featured: string;
  service: string;
}

type MainFeaturedItem = {
  image: string;
  title: string;
  location: string;
  url: string;
}

type MainServiceItem = {
  image: string;
  content: string;
  code: string;
}

type ProjectItem = {
  sq: number;
  slug: string;
  title: string;
  type: string;
  client: string;
  access: 'Y' | 'N';
  password: string;
  logoImage: string;
  footerLogoImage: string;
  ogImage: string;
  resource: string | FileItem[];
  registerDtt: string;
  updateDtt: string;
  hasPassword: boolean;
}

type PasswordItem = {
  sq: number;
  password: string;
}

type ArticleCopyItem = {
  copyLang: string;
  slug: string;
  title: string;
}

type ManagerItem = {
  id: string;
  name: string;
  pwd: string;
  regDtt: string;
  roles: Role;
  lastLogin: string;
  status: number;
  wrongCount: number;
  pwdChangeDtt: string;
  quitDtt: string;
  quitReason: string;
  pwdAge: number;
  loginAge: number;
}

type AccessLogItem = {
  id: string;
  name: string;
  menu: string;
  action: string;
  size: string;
  dtt: string;
  ipAddr: number;
}

type LoginLogItem = {
  id: string;
  name: string;
  dtt: string;
  ipAddr: number;
}

type Paging = { pageNo: number, perPage: number, listCount: number };

type PagingList<T> = {
  list: T[];
  paging: Paging
}

type FileItem = {
  sq: string;
  location: string;
  name?: string;
  fileName?: string;
  capacity?: string;
  contentType?: string;
  description?: string | null;
  filekey?: string;
  lang?: string | null;
  mediaType?: string;
  added?: boolean;
  file?: File;
}

type FileInputModel = {
  remove: string[];
  exist: FileItem[];
  add: Record<string, File>;
  toString: () => 'FILE_INPUT_MODEL'
}

type PeopleItem = {
  sq: number;
  lang: string;
  image: string;
  name: string;
  position: string;
  email: string;
  introduction: string;
  licenses: number;
  projects: string;
  registerDtt: string;
}

type PeopleProjectItem = {
  year: string;
  title: string;
  url: string;
}