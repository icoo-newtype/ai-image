export type SelectItem = { label: string, value: string | number } | string;
export type ListObject = Record<string | number, string>;
export type ObjectParam = Record<string, string | number>;
export type ListComponentProps = { list?: SelectItem[], split?: string, obj?: ListObject };
export type SelectComponentProps = ListComponentProps & {
  variant?: string,
  required?: boolean,
  readonly?: boolean,
  modelValue?: string | number,
  placeholder?: string
};
export type SelectComponentEmits = { change: [string | number | null], 'update:modelValue': [string | number | null] };
export type FilterComponentProps = { modelValue?: ObjectParam };
export type FilterComponentEmits = { change: [ObjectParam], 'update:modelValue': [ObjectParam] };
export type ModuleItem = { comp: string, name: string, type?: string, value?: ObjectParam[] | ObjectParam | null };
