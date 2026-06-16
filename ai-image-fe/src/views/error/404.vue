<script setup lang="ts"></script>
<template>
  <div error-404>
    <header>
      <a href="/admin/" class="logo"><h1 class="ir">newtype</h1></a>
    </header>
    <h2>페이지를 찾을 수 없습니다.</h2>
    <p>잘못된 접근이거나 요청하신 페이지를 찾을 수 없습니다. <br>입력하신 페이지의 주소가 정확한지 다시 한번 확인해 주시기 바랍니다.</p>
    <!--    <a href="/admin/" class="btn-home">홈으로 가기</a>-->
  </div>
</template>
<style lang="less">
@import '~@/less/proj';
.ir { .ir; }

[error-404] { .rel; .h(100vh); .flex; .flex-column; .justify-center; .items-center; .tc; .ph(16);
  header { .fix; .lt;
    a.logo { .block; .w(43); .abs; .lt(24, 24); .contain('/admin_image/logo-l.png'); z-index: 2; }
  }
  h2 { .fs(36, 1); .bold; }
  h2 + p { .mt(24); .fs(14, 1.6); }
  .btn-home { .mt(40); .p(0, 30); .h(40); .fs(16, 40); .c(#fff); .bgc(#000); .br(3); }
}
</style>