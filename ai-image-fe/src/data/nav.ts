export type NavItem = { name: string, url: string, lang?: string[], children?: NavItem[] };
export const navData = [
  {
    lang: ['ko', 'en', 'zh'],
    name: 'Main',
    url: '/admin/main',
    children: [
      {
        name: '메인 콘텐츠 관리',
        url: '/admin/main',
      },
    ],
  },
  {
    lang: ['ko', 'en', 'zh'],
    name: 'Project',
    url: '/admin/project',
    children: [
      {
        name: '모든 게시물',
        url: '/admin/project/list',
      },
      {
        name: '새 게시물 등록',
        url: '/admin/project/article',
      },
    ],
  },
  {
    lang: ['ko', 'en', 'zh'],
    name: 'News',
    url: '/admin/news',
    children: [
      {
        name: '모든 게시물',
        url: '/admin/news/list',
      },
      {
        name: '새 게시물 등록',
        url: '/admin/news/article',
      },
    ],
  },
  {
    lang: ['ko', 'en', 'zh'],
    name: 'Leadership',
    url: '/admin/leadership',
    children: [
      {
        name: '모든 게시물',
        url: '/admin/leadership/list',
      },
      {
        name: '새 게시물 등록',
        url: '/admin/leadership/article',
      },
    ],
  },
  {
    lang: ['ko', 'en', 'zh'],
    name: 'Manager',
    url: '/admin/manager',
    children: [
      {
        name: '모든 카테고리',
        url: '/admin/manager/category',
      },
      {
        name: '모든 관리자',
        url: '/admin/manager/list',
      },
      {
        name: '접속로그',
        url: '/admin/manager/loginLog',
      },
      {
        name: '운영로그',
        url: '/admin/manager/accessLog',
      },
    ],
  },
] as NavItem[];