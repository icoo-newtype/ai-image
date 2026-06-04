import path from 'path';
import { existsSync, mkdirSync, readdirSync, readFileSync, unlinkSync, writeFileSync } from 'fs';
import * as minimatch from "minimatch";
import type { PluginOption } from 'vite';

export const componentsMapBuilder = (folder: string): PluginOption => {
  const build = () => new Promise<void>(resolve => {
    const moduleFolder = path.join(process.cwd(), folder);
    const output = path.join(moduleFolder, `index.ts`);
    if (!existsSync(moduleFolder)) mkdirSync(moduleFolder);
    const imports = [];
    const modules = [];
    for (const file of readdirSync(moduleFolder)) {
      if (file === 'index.ts') continue;
      if (file === 'Module.vue') continue;
      const m = file.split('.');
      if (m) {
        imports.push(`import ${m[0]} from './${file}';`);
        modules.push(m[0]);
      }
    }
    const str = `${imports.join('\n')}\nexport const getComponentsMap = (): Record<string, any> => ({ ${modules.join(', ')} });`;
    if (existsSync(output)) {
      const org = readFileSync(output, 'utf-8');
      if (str === org) return resolve();
      unlinkSync(output);
    }
    writeFileSync(output, str);
    resolve();
  });

  return {
    name: 'ComponentsMapBuilder',
    buildStart() {
      return build();
    },
    handleHotUpdate({ file, server }) {
      const pattern = folder + '/*.vue';
      const matched = minimatch.minimatch(file, path.join(server.config.root, pattern));
      if (!matched) return;

      return build();
    },
  };
};