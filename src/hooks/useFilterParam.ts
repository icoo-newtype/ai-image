import { reactive, ref, watch } from 'vue';
import { FilterComponentEmits, FilterComponentProps, ObjectParam } from '@/types/components';

export const useFilterParam = (props: FilterComponentProps & { prop: string }, emit: EmitHandler<FilterComponentEmits>) => {
  const model = ref<string | number>();
  const assign = () => {
    model.value = props.modelValue?.[props.prop]
  };
  assign();
  watch(() => props.modelValue, assign);
  const commit = () => {
    const val = { ...props.modelValue, [props.prop]: model.value } as ObjectParam;
    emit('update:modelValue', val);
    emit('change', val);
  };
  return { model, commit };
}

export const useFilterParams = (props: FilterComponentProps, emit: EmitHandler<FilterComponentEmits>, useProps: string[] ) => {
  const model = reactive<ObjectParam>({});
  const assign = () => {
    useProps.forEach(name => {
      if (props.modelValue?.[name] !== undefined) model[name] = props.modelValue[name];
    });
  };
  assign();
  watch(() => props.modelValue, assign);
  const commit = () => {
    const val = { ...props.modelValue, ...model };
    emit('update:modelValue', val);
    emit('change', val);
  };
  return { model, commit };
}