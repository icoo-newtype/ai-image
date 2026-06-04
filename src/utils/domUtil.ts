export const closest = (e: HTMLElement, selector: string) => {
  if (e.closest) return e.closest(selector) as HTMLElement;
  let t: HTMLElement | null = e;
  while(t) {
    if (t.matches(selector)) return t as HTMLElement;
    if (t.parentNode === t) return null;
    t = t.parentNode as HTMLElement;
  }
  return null;
}

export const filterNumericInput = (event: InputEvent) => {
  if (!event.target) return;
  const input = event.target as HTMLInputElement;
  input.value = input.value.replace(/\D/g, '');
};