import { useHead } from '@vueuse/head';

export const useSocialHead = ({ title, description, image }: { title?: string, description?: string, image?: string }) => {
  if (!title && !description && !image) return;
  const result: any = {
    meta: [
      { name: 'twitter:card', content: 'summary_large_image' },
    ]
  };
  if (title) {
    result.title = title;
    result.meta.push({ property: 'og:title', content: title });
    result.meta.push({ name: 'twitter:title', content: title });
  }

  if (description) {
    result.meta.push({ property: 'og:description', content: description });
    result.meta.push({ name: 'twitter:description', content: description });
  }

  if (image) {
    result.meta.push({ property: 'og:image', content: image });
    result.meta.push({ name: 'twitter:image', content: image });
  }

  useHead(result);
};