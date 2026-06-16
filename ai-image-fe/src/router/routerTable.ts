import { RouterView } from 'vue-router';

const page = (path: string) => () => import((`@/views/pages/admin/${path}.vue`));

export default [
  {
    name: '비밀번호 변경',
    path: 'my/password',
    component: page('Password'),
    props: { type: 'my' },
  },
  {
    path: 'project',
    component: RouterView,
    children: [
      {
        path: '/',
        redirect: 'list',
      },
      {
        meta: { label: 'Project List' },
        path: 'list',
        component: page('ProjectList'),
      },
      {
        meta: { label: '새 프로젝트 등록' },
        path: 'article',
        component: page('ProjectItem'),
      },
      {
        meta: { label: '프로젝트 수정' },
        path: 'article/:sq',
        component: page('ProjectItem'),
      },
      {
        meta: { label: '프로젝트 상세페이지 편집' },
        path: ':slug/:code',
        component: page('ProjectArticle'),
      },
    ],
  },
  {
    meta: { label: 'Manager' },
    path: 'manager',
    component: RouterView,
    children: [
      {
        path: '',
        redirect: 'list',
      },
      {
        name: '모든 카테고리',
        path: 'category',
        component: page('Category'),
      },
      {
        name: '모든 관리자',
        path: 'list',
        component: page('ManagerList'),
      },
      {
        name: '접속로그',
        path: 'loginLog',
        component: page('LoginLog'),
      },
      {
        name: '운영로그',
        path: 'accessLog',
        component: page('AccessLog'),
      },
    ],
  },
];
