<script setup lang="ts">
import Breadcrumb from '@/views/components/common/Breadcrumb.vue';
import { useAuthInfo } from '@/store/auth';
import { useState } from '@/store/state';
import BDropdown from '@/views/components/common/BDropdown.vue';
import BDropdownItem from '@/views/components/common/BDropdownItem.vue';
import { removeCookie } from '@/utils';

const authInfo = useAuthInfo();

const logout = () => {
  removeCookie('authToken');
  location.reload();
};

</script>
<template>
  <div v-cloak class="nt-app">
    <div class="app-header">
      <router-link to="/admin/" class="logo">
        <img alt="logo" src="/admin_image/logo-l.png">
      </router-link>
      <Breadcrumb/>
      <div class="account-info">
        <b-dropdown :text="`${authInfo.user?.name}`" variant="ghost">
          <!--          <b-dropdown-item to="/admin/my/password"><i class="icon-lock"></i> 비밀번호변경</b-dropdown-item>-->
          <b-dropdown-item @click="logout"><i class="icon-logout"></i> 로그아웃</b-dropdown-item>
        </b-dropdown>
      </div>
    </div>
    <main>
      <router-view/>
    </main>
  </div>
</template>

<style lang="less">
@import '@/less/proj.less';

.nt-app { .rel;
  .app-header { .flex; justify-content: space-between; align-items: center; .p(0, 40); .-b(#E7E7E7); .h(49); .z(30);
    .logo img { .block; .h(24); }
    .breadcrumb { .m; .-a; .bgc(transparent);
      .breadcrumb-item {
        a {color: #898989;}
        .active {font-weight: 500;}
      }
      .breadcrumb-item + .breadcrumb-item:before { content: '|' }
    }
    .account-info {
      .dropdown-menu { .w(150); .-a(@c-border); .l(auto); .r(-20); }
      .dropdown-item {
        &:hover, &:active { .bgc(@c-line-bg-gray); }
      }
      .btn { .p(14, 20); .fs(13); width: auto; height: auto;
        &:after { .hide; }
      }
    }
  }
  main { .pt(50); .pb(150); }
}
</style>