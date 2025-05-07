interface PreviewOptions {
  title: string;
  pdfUrl: string;
  token: string;
}

const createPreviewContent = ({ title, pdfUrl, token }: PreviewOptions): string => {
  const htmlContent = [
    '<!DOCTYPE html>',
    '<html>',
    '<head>',
    '<meta charset="UTF-8">',
    `<title>简历预览 - ${title}</title>`,
    '<style>',
    'body { margin: 0; padding: 0; overflow: hidden; background: #f0f2f5; }',
    '.container { display: flex; flex-direction: column; height: 100vh; }',
    '.loading { position: fixed; top: 50%; left: 50%; transform: translate(-50%, -50%); font-family: Arial, sans-serif; color: #666; text-align: center; }',
    '.loading .spinner { margin-bottom: 10px; }',
    '.error { color: #f56c6c; }',
    'object { width: 100%; height: 100%; border: none; }',
    '</style>',
    '</head>',
    '<body>',
    '<div class="container">',
    '<div class="loading">',
    '<div class="spinner">正在加载简历...</div>',
    '</div>',
    '<object type="application/pdf">',
    '<p>无法直接预览PDF，请<a href="#" onclick="downloadPDF()">点击下载</a></p>',
    '</object>',
    '</div>',
    '<script>',
    'function showError(message) {',
    '  const loading = document.querySelector(".loading");',
    '  if (loading) {',
    '    loading.innerHTML = `<div class="error">${message}</div>`;',
    '  }',
    '}',
    '',
    'function loadPDF() {',
    '  const xhr = new XMLHttpRequest();',
    `  xhr.open("GET", "${pdfUrl}", true);`,
    `  xhr.setRequestHeader("Authorization", "Bearer ${token}");`,
    '  xhr.responseType = "blob";',
    '',
    '  xhr.onload = function() {',
    '    if (xhr.status === 200) {',
    '      const blob = new Blob([xhr.response], { type: "application/pdf" });',
    '      const url = window.URL.createObjectURL(blob);',
    '      const obj = document.querySelector("object");',
    '      if (obj) {',
    '        obj.data = url;',
    '        const loading = document.querySelector(".loading");',
    '        if (loading) loading.style.display = "none";',
    '      }',
    '    } else {',
    '      showError("加载PDF失败，请稍后重试");',
    '    }',
    '  };',
    '',
    '  xhr.onerror = function() {',
    '    showError("加载PDF失败，请检查网络连接");',
    '  };',
    '',
    '  xhr.send();',
    '}',
    '',
    'function downloadPDF() {',
    '  const xhr = new XMLHttpRequest();',
    `  xhr.open("GET", "${pdfUrl}", true);`,
    `  xhr.setRequestHeader("Authorization", "Bearer ${token}");`,
    '  xhr.responseType = "blob";',
    '',
    '  xhr.onload = function() {',
    '    if (xhr.status === 200) {',
    '      const blob = new Blob([xhr.response], { type: "application/pdf" });',
    '      const url = window.URL.createObjectURL(blob);',
    '      const a = document.createElement("a");',
    '      a.style.display = "none";',
    '      a.href = url;',
    '      a.download = "resume.pdf";',
    '      document.body.appendChild(a);',
    '      a.click();',
    '      document.body.removeChild(a);',
    '      window.URL.revokeObjectURL(url);',
    '    } else {',
    '      showError("下载PDF失败，请稍后重试");',
    '    }',
    '  };',
    '',
    '  xhr.onerror = function() {',
    '    showError("下载PDF失败，请检查网络连接");',
    '  };',
    '',
    '  xhr.send();',
    '}',
    '',
    'document.addEventListener("DOMContentLoaded", function() {',
    '  loadPDF();',
    '});',
    '</script>',
    '</body>',
    '</html>'
  ].join('\n');

  return htmlContent;
};

export const openPdfPreview = (options: PreviewOptions): void => {
  const width = Math.min(window.screen.width * 0.8, 1024);
  const height = Math.min(window.screen.height * 0.8, 800);
  const left = (window.screen.width / 2) - (width / 2);
  const top = (window.screen.height / 2) - (height / 2);

  const pdfWindow = window.open(
    '',
    '_blank',
    `toolbar=no, location=no, directories=no, status=no, menubar=no, scrollbars=yes, resizable=yes, copyhistory=no, width=${width}, height=${height}, top=${top}, left=${left}`
  );

  if (pdfWindow) {
    const content = createPreviewContent(options);
    pdfWindow.document.open();
    pdfWindow.document.write(content);
    pdfWindow.document.close();
    return;
  }

  throw new Error('无法打开预览窗口');
}; 