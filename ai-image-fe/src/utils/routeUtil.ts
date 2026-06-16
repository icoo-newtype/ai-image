import { useRouter } from 'vue-router';
import { watch } from 'vue';


export const reloadPage = () => {
  const { currentRoute } = useRouter();
  watch(currentRoute, () => location.reload());
};