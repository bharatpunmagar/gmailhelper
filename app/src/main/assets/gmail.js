(function () {
  'use strict';
  if (window.__g) return;

  const panel = document.createElement('div');
  panel.style.cssText =
    'position:fixed;left:0;right:0;bottom:0;z-index:2147483647;max-height:45%;' +
    'overflow:auto;background:rgba(5,8,12,.95);color:#8ef0a8;' +
    'font:12px/1.5 ui-monospace,monospace;padding:8px 10px;' +
    'border-top:2px solid #2c3340;display:none;white-space:pre-wrap';

  const visible = el => {
    if (!el) return false;
    const r = el.getBoundingClientRect();
    return r.width > 2 && r.height > 2;
  };

  function collect() {
    const out = [];
    const seen = new Set();
    const nodes = document.querySelectorAll(
      'button, a, div[role="button"], [role="link"], [jsname]');
    for (const el of nodes) {
      if (!visible(el)) continue;
      const t = (el.innerText || '').trim().replace(/\s+/g, ' ');
      if (!t || t.length > 90) continue;
      if (seen.has(t)) continue;
      seen.add(t);
      out.push({ el, t });
      if (out.length > 40) break;
    }
    return out;
  }

  function render() {
    const items = collect();
    panel.innerHTML = 'विकल्प (' + items.length + '):<br>';
    for (const it of items) {
      const row = document.createElement('div');
      row.textContent = '• ' + it.t;
      row.style.cssText = 'padding:4px 0;cursor:pointer;border-bottom:1px solid #1b2129';
      row.onclick = () => { it.el.scrollIntoView(); it.el.click(); };
      panel.appendChild(row);
    }
    panel.style.display = items.length ? 'block' : 'none';
  }

  window.__g = {
    list() { render(); return String(collect().length); },
    tryAnother() {
      const re = /try another way|more options|अर्को तरिका|अन्य तरिका|didn'?t get a code|resend/i;
      for (const el of document.querySelectorAll('button, a, div[role="button"]')) {
        if (!visible(el)) continue;
        const t = (el.innerText || '').trim();
        if (re.test(t)) { el.click(); return t; }
      }
      return 'भेटिएन';
    }
  };

  setTimeout(render, 1800);
})();
