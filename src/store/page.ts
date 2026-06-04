import { defineStore } from 'pinia';
import { computed, ref } from 'vue';
import { ModuleItem } from '@/types/components';
import { RouteParamValue } from 'vue-router';

export type PageItem = {
  projSq: number;
  parentCode: string;
  code: string;
  label: string;
  data: Record<string, any>;
  access: 'Y' | 'N';
  odr: number;
  edit: boolean;
  article: string;
  children?: PageItem[];
  anchors: Record<string, string>[];
  anchorsNum: number;
};

export type PageResponse = PageItem & { data: string };

export const usePage = defineStore('pages', () => {
  // 프로젝트 정보
  const info = ref<ProjectItem | null>(null);

  // 전체 페이지 목록 (flat)
  const codes = ref<PageItem[]>([]);

  // 현재 활성 페이지
  const current = ref<PageItem | null>(null);

  // ── 유틸 ───────────────────────────────────────────

  const codeList = (par: string, omit?: string[]) => {
    return codes.value
      .filter(row =>
        row.parentCode === par &&
        (omit ? omit.every(o => `${par}-${o}` !== row.code) : true)
      )
      .map(code => ({ value: code.code, label: code.label }));
  };

  const codeLabel = (code: string) =>
    codes.value.find(row => row.code === code)?.label ?? null;

  const findByCode = (code: string) =>
    codes.value.find(row => row.code === code) ?? null;

  // ── 트리 ───────────────────────────────────────────

  function buildTree(parentCode = 'ROOT'): PageItem[] {
    const build = (parentCode: string): PageItem[] =>
      codes.value
        .filter(item => item.parentCode === parentCode)
        .sort((a, b) => a.odr - b.odr)
        .map(item => ({ ...item, children: build(item.code) }));
    return build(parentCode);
  }

  const menuTree = computed(() => buildTree());

  // ── 액션 ───────────────────────────────────────────

  function setInfo(project: ProjectItem) {
    info.value = project;
  }

  function setCodes(rows: PageResponse[]) {
    codes.value = rows.map(row => ({
      ...row,
      data: typeof row.data === 'string' ? JSON.parse(row.data) : row.data,
    }));
  }

  function setCurrent(code: string | RouteParamValue[]) {
    current.value = findByCode(code as string);
    if (!current.value) return;
    const flat = JSON.parse(decodeURIComponent(atob(current.value?.article as string)))
      .filter((item: ModuleItem) => item.type === 'title' || item.type === 'header')
      .map((item: ModuleItem) => ({
        name: item.name,
        title: (item.value as Record<string, string>)?.title ?? ''
      }));
    current.value.anchorsNum = flat.length;
    current.value.anchors = flat.reduce((acc: any[][], item: any) => {
      if (item.name === 'Header') acc.push([]);
      acc.at(-1)?.push(item);
      return acc;
    }, []);
  }

  return {
    info,
    codes,
    current,
    menuTree,
    codeList,
    codeLabel,
    findByCode,
    setInfo,
    setCodes,
    setCurrent,
    buildTree,
  };
});