// main.server.ts
import 'zone.js/node';
import { renderApplication } from '@angular/platform-server';
import { AppComponent } from './app/app.component';
import { config } from './app/app.config.server';

export default function render(url: string, document: string) {
  return renderApplication(() => AppComponent, {
    document,
    url,
    providers: [
      ...(config.providers || [])
    ]
  });
}
