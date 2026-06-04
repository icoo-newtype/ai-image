import { ObjectDirective, Ref } from 'vue';
import { MediaRule, RuleLike } from '@/plugins/parallax';
import { SwiperOptions } from 'swiper/types/swiper-options';
import Swiper from 'swiper';

declare module '@vue/runtime-core' {
  export interface ComponentCustomProperties {
    vPrx: ObjectDirective<HTMLElement, MediaRule | RuleLike>
    vPrxItem: ObjectDirective
    vPrxSection: ObjectDirective
    vSlide: ObjectDirective<HTMLElement, { options?: SwiperOptions, created?: (swiper: Swiper) => void }>
  }
}