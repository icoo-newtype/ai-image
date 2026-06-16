<script setup lang="ts">
import { inject, ref, watch } from 'vue';
import Modal from '@/views/components/common/Modal.vue';
import BCard from '@/views/components/common/BCard.vue';
import { ModuleItem } from '@/types/components';
import BButton from '@/views/components/common/BButton.vue';
import BButtonGroup from '@/views/components/common/BButtonGroup.vue';
import RadioButton from '@/views/components/form/RadioButton.vue';
import BCheckbox from '@/views/components/common/BCheckbox.vue';
import BSwtich from '@/views/components/common/BSwtich.vue';

const emit = defineEmits<{ resolve: [ModuleItem], reject: [] }>();
const open = ref(true);

// name:모듈이름, comp:컴포넌트, type:동일컴포넌트 내 구분 class, value:컴포넌트 내 인풋 값
const selected = ref({ active: 'Header' });
const tabList = ref([
  {
    name: 'Text',
    subList: ['Header', 'Text'],
  },
  {
    name: 'Media',
    subList: ['Embeded', 'Slider'],
  },
  {
    name: 'Button',
    subList: ['Button'],
  },
  // {
  //   name: 'Video',
  //   subList: ['Video Upload'],
  // },
  {
    name: 'Appendix',
    subList: ['Blank', 'Color Palette', 'Resources'],
  },
]);
const mediaCol2type = ref('basic');
const mediaCol3type = ref('basic');
const colorCol2title = ref<boolean>(false);
const colorCol4title = ref<boolean>(false);

const resolve = (module: ModuleItem) => {
  emit('resolve', { ...module });
  open.value = false;
};

watch(() => colorCol2title.value, () => {
});
</script>

<template>
  <Modal v-model="open" module-modal size="xl">
    <div class="card-container mb-0">
      <div class="tabs">
        <h4 class="title">모듈 추가</h4>
        <div class="tab-scroll">
          <div class="inner" v-for="(list,i) in tabList" :key="i">
            <p class="rep">{{
                list.name
              }}</p>
            <ul v-if="list.subList !== null">
              <li v-for="(subTab,j) in list.subList" :key="j" @click="selected = {active:subTab}" :class="selected.active === subTab  ? 'active' : null">
                {{ subTab }}
              </li>
            </ul>
          </div>
        </div>
      </div>
      <div class="contents">
        <template v-if="selected.active === 'Header'">
          <b-card @click="resolve({ name:'Header', comp: 'TitleText', type:'header', value: null })">
            <div class="text">
              <p class="title">Header</p>
            </div>
            <div class="view header">
              <p>페이지 타이틀을 입력해 주세요</p>
              <p>텍스트를 입력해 주세요</p>
            </div>
          </b-card>
        </template>
        <template v-if="selected.active === 'Text'">
          <b-card @click="resolve({ name:'Title', comp: 'TitleText', type:'title', value: null })">
            <div class="text">
              <p class="title">Title</p>
            </div>
            <div class="view title">
              <p>섹션 타이틀을 입력해 주세요</p>
              <p>텍스트를 입력해 주세요</p>
            </div>
          </b-card>
          <b-card @click="resolve({ name:'Subtitle', comp: 'TitleText', type:'sub-title', value: null })">
            <div class="text">
              <p class="title">Subtitle</p>
            </div>
            <div class="view sub-title">
              <p>서브 타이틀을 입력해 주세요</p>
              <p>텍스트를 입력해 주세요</p>
            </div>
          </b-card>
        </template>
        <template v-if="selected.active === 'Embeded'">
          <b-card @click="resolve({ name:'1 Column', comp: 'Media', type:'basic', value: [{media: '', caption: ''}] })">
            <div class="text">
              <p class="title">1 Column</p>
            </div>
            <div class="view media">
              <div class="img"><img src="/admin_image/module/img.svg" alt=""></div>
            </div>
          </b-card>
          <b-card @click="resolve({ name:'2 Column', comp: 'Media', type: mediaCol2type, value: [{media: '', caption: ''},{media: '', caption: ''}] })">
            <div class="text">
              <p class="title">2 Column</p>
              <RadioButton v-model="mediaCol2type" :obj="{ 'basic':'Basic', 'incorrect':'Incorrect Usage', 'correct':'Correct Usage' }"/>
            </div>
            <div class="view media">
              <div class="img"><img src="/admin_image/module/img.svg" alt=""></div>
              <div class="img"><img src="/admin_image/module/img.svg" alt=""></div>
            </div>
          </b-card>
          <b-card @click="resolve({ name:'3 Column', comp: 'Media', type: mediaCol3type, value: [{media: '', caption: ''},{media: '', caption: ''},{media: '', caption: ''}] })">
            <div class="text">
              <p class="title">3 Column</p>
              <RadioButton v-model="mediaCol3type" :obj="{ 'basic':'Basic', 'incorrect':'Incorrect Usage', 'correct':'Correct Usage' }"/>
            </div>
            <div class="view media">
              <div class="img"><img src="/admin_image/module/img.svg" alt=""></div>
              <div class="img"><img src="/admin_image/module/img.svg" alt=""></div>
              <div class="img"><img src="/admin_image/module/img.svg" alt=""></div>
            </div>
          </b-card>
          <b-card @click="resolve({ name:'3 Column', comp: 'Media', type: mediaCol3type, value: [{media: '', caption: ''},{media: '', caption: ''},{media: '', caption: ''},{media: '', caption: ''},{media: '', caption: ''},{media: '', caption: ''}] })">
            <div class="text">
              <p class="title">3 Column</p>
              <RadioButton v-model="mediaCol3type" :obj="{ 'basic':'Basic', 'incorrect':'Incorrect Usage', 'correct':'Correct Usage' }"/>
            </div>
            <div class="view media row-2">
              <div class="img"><img src="/admin_image/module/img.svg" alt=""></div>
              <div class="img"><img src="/admin_image/module/img.svg" alt=""></div>
              <div class="img"><img src="/admin_image/module/img.svg" alt=""></div>
              <div class="img"><img src="/admin_image/module/img.svg" alt=""></div>
              <div class="img"><img src="/admin_image/module/img.svg" alt=""></div>
              <div class="img"><img src="/admin_image/module/img.svg" alt=""></div>
            </div>
          </b-card>
          <b-card @click="resolve({ name:'Video', comp: 'Video', type:'basic', value: null })">
            <div class="text">
              <p class="title">Video</p>
            </div>
            <div class="view media">
              <div class="img"><img src="/admin_image/module/video.svg" alt=""></div>
            </div>
          </b-card>
        </template>
        <template v-if="selected.active === 'Slider'">
          <b-card @click="resolve({ name:'Slider', comp: 'Slider', type:'slider', value: null })">
            <div class="text">
              <p class="title">Slider</p>
              <p class="sum">
                이미지 업로드 시 원본 사이즈(x1)로 노출됩니다. <br>
                권장 사이즈 : <b>600x600 / 1067x600 / 480x600 </b></p>
            </div>
            <div class="view slider">
              <div class="box"><img src="/admin_image/module/img.svg" alt=""></div>
              <div class="box"><img src="/admin_image/module/img.svg" alt=""></div>
              <div class="box"><img src="/admin_image/module/img.svg" alt=""></div>
              <div class="box"><img src="/admin_image/module/img.svg" alt=""></div>
              <div class="box"><img src="/admin_image/module/img.svg" alt=""></div>
              <div class="box"><img src="/admin_image/module/img.svg" alt=""></div>
            </div>
          </b-card>
        </template>
        <template v-if="selected.active === 'Button'">
          <b-card @click="resolve({ name:'Download', comp: 'Download', type:'download', value: null })">
            <div class="text">
              <p class="title">Download</p>
            </div>
            <div class="view download">
              <b-button variant="primary">Download</b-button>
            </div>
          </b-card>
        </template>
        <template v-if="selected.active === 'Blank'">
          <b-card @click="resolve({ name:'Blank', comp: 'Blank', type: 'blank' })">
            <div class="text">
              <p class="title">Blank</p>
              <p class="sum">여백 모듈 추가 후 높이값 설정 가능</p>
            </div>
            <div class="view blank"></div>
          </b-card>
        </template>
        <template v-if="selected.active === 'Color Palette'">
          <b-card>
            <div class="text">
              <p class="title">2 Column</p>
              <b-swtich class="mv-20" v-model="colorCol2title" variant="success" label="타이틀 설정"/>
            </div>
            <div class="view color" @click="resolve({ name:'2 Column', comp: 'ColorPalette', type: colorCol2title ? 'col-2-title' : 'col-2'})">
              <p>Color title</p>
              <p>Color subtitle</p>
              <ul class="column-2">
                <li>
                  <div class="color"></div>
                  <p>#C5BDFF</p></li>
                <li>
                  <div class="color"></div>
                  <p>#C5BDFF</p></li>
              </ul>
            </div>
          </b-card>
          <b-card>
            <div class="text">
              <p class="title">4 Column</p>
              <b-swtich class="mv-20" v-model="colorCol4title" variant="success" label="타이틀 설정"/>
            </div>
            <div class="view color" @click="resolve({ name:'4 Column', comp: 'ColorPalette', type: colorCol4title ? 'col-4-title' : 'col-4'})">
              <p>Color title</p>
              <p>Color subtitle</p>
              <ul class="column-4">
                <li>
                  <div class="color"></div>
                  <p>#C5BDFF</p></li>
                <li>
                  <div class="color"></div>
                  <p>#C5BDFF</p></li>
                <li>
                  <div class="color"></div>
                  <p>#C5BDFF</p></li>
                <li>
                  <div class="color"></div>
                  <p>#C5BDFF</p></li>
              </ul>
            </div>
          </b-card>
        </template>
        <template v-if="selected.active === 'Resources'">
          <b-card>
            <div class="text">
              <p class="title">Basic</p>
            </div>
            <div class="view resource" @click="resolve({ name:'Resource', comp: 'Resource', type: 'resource'})">
              <div class="box">
                <div class="img"><img src="/admin_image/module/img.svg" alt=""></div>
                <div>
                  <p>파일명을 입력해 주세요</p>
                  <p>파일 사이즈를 단위와 함께 입력해 주세요.</p>
                </div>
              </div>
              <div class="box">
                <div class="img"><img src="/admin_image/module/img.svg" alt=""></div>
                <div>
                  <p>파일명을 입력해 주세요</p>
                  <p>파일 사이즈를 단위와 함께 입력해 주세요.</p>
                </div>
              </div>
            </div>
          </b-card>
        </template>
      </div>
    </div>
  </Modal>
</template>

<style lang="less">
@import '~@/less/proj';

[module-modal] { .rel;
  .modal-content { .-a; }
  .modal-header { .abs; .rt; .p;
    button {color: #fff; z-index: 19}
  }
  .modal-body {.h(70vh); .p(0); .m(0) !important;}
  .card-container {.br(2); .hf; .flex; .min-h(450); }
  .tabs { .w(285); .bgc(#fff); flex-basis: 20%; .rel;
    .title {.wf; .bold; .h(20%); .p(32, 32, 50); .m(0); .bgc(#fff); .fs(28); color: #1c1c1c;}
    .tab-scroll {overflow-y: scroll; .h(80%);}
    .inner {
      .rep {.p(10, 32); .m(0); color: #a9a9a9; .fs(13, 1.85); .medium;}
      ul {
        li { .fs(18, 1.3); .p(10, 32); cursor: pointer; .medium;
          &.active {.bgc(#f8f8f8);}
        }
      }
    }
  }
  .contents { .p(48, 60); flex-basis: 80%; overflow-y: scroll; .bgc(#f3f5f7);
    .card { .pointer; .br(0); .-a; .bgc(transparent);
      .card-body {.p(0, 0, 48);
        .title {.fs(20, 1.2); .mb(14); .medium; color: #1c1c1c;}
        .sum {.mb(24); .fs(16, 1.5); color: #666; }
        > img {.wf; .-a(2px, #fff);}
        .btn-group { .mv(20);
          .btn-default { .bgc(#fff); .-a(#fff); }
        }
        .view { .br(5); .bgc(#fff); .-a(#fff); .c(#666); transition: background-color .2s ease;
          &:hover { .bgc(#e0e0e0); .-a(#e0e0e0); }
          &.header { .p(50, 50, 60);
            p { .bold; .fs(56); }
            p + p { .mt(120); .fs(20); }
          }
          &.title { .grid(2); .p(50, 50, 60);
            p { .medium; .fs(32); }
            p + p { .fs(14); }
          }
          &.sub-title { .p(0, 50, 40);
            p { .medium; .fs(18); }
            p + p { .mt(8); .fs(12); }
          }
          &.blank { .h(100); }
          &.slider { .grid(6, 8); .p(8);
            .box { .rel; .pt(100%); .bgc(#f4f4f4);
              img { .block; .abs; .lt(50%, 50%); .t-xyc; }
            }
          }
          &.download { .pl(52); .pb(62);
            .btn { .rel; .pr(40);
              &:after { .cnt; .abs; .rt(12, 50%); .t-yc; .wh(16); .contain('/image/common/ico-download-w.svg'); }
            }
          }
          &.media { .flex-center; .min-h(200); .p(20);
            .img { .br(5); .flex-center; .bgc(#F4F4F4); .w(30%); .min-h(200) }
            .img + .img { .ml(10); }
            &.row-2 { .grid(3, 10);
              .img { .wf; }
              .img + .img { .ml(0); }
            }
          }
          &.color { .ph(52);
            p { .fs(16); .semi-bold; }
            p + p { .mt(15); .fs(14); .o(0.2); }
            ul { .mt(10);
              &.column-2 { .grid(2, 15); }
              &.column-4 { .grid(4, 15); }
              li {
                .color { .h(150); .br(8); .bgc(#eee); }
                .color + p { .mt(6); .fs(10); }
              }
            }
          }
          &.resource { .ph(52); .grid(2);
            .box { .flex; .pv(15); }
            .img { .br(5); .flex-center; .bgc(#F4F4F4); .wh(150, 90); .mr(20); }
            p { .fs(14); .c(#666); }
            p + p { .mt(6); .fs(10); }
          }
        }
      }
      &.on {
        .card-body > img, .view {.-a(2px, rgba(0, 76, 152, 0.3)); }
      }
    }
  }
  .modal-footer {.abs; .rb(0, 0);}
}
@media (@dm-up) {
  .modal-xl { max-width: 1462px; }
}
</style>
