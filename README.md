# ⚡ Livewire Component Folder

A PhpStorm plugin that enhances the project tree view for Laravel Livewire components by organizing multi-file components into a clean, collapsible structure.

## Features

### 🎯 Smart Component Display
- Displays component name (from files) instead of folder name
- Shows lightning bolt icon (⚡) for quick identification
- Child files shown as extensions only (.blade.php, .php, .css, .js)
- Supports CSS and JavaScript files alongside PHP and Blade templates

### 🖱️ Customizable Double-Click Actions
Choose what happens when you double-click a component:
- **Expand/Collapse** - Standard folder behavior (default)
- **Open PHP class** - Opens the component's PHP file
- **Open Blade template** - Opens the Blade view file
- **Open selected files** - Opens files you've selected with customizable layout

### 📐 Multiple Split Layouts
When opening multiple files, choose how they appear:
- **Tabs** - Open files in separate tabs (no split)
- **Side by side** - Vertical split for side-by-side viewing
- **Stacked** - Horizontal split for stacked viewing

### ⚙️ Flexible File Selection
Select which file types to open:
- PHP class (.php)
- Blade template (.blade.php)
- CSS file (.css or global.css)
- JavaScript file (.js)

### 🔍 Auto-Detection
- Scans configured paths (default: `resources/views`)
- Optionally reads paths from `config/livewire.php`
- Detects components with matching PHP and Blade files

## Installation

### From JetBrains Marketplace (Coming Soon)
1. Open PhpStorm
2. Go to Settings → Plugins
3. Search for "Livewire Component Folder"
4. Click Install

### Manual Installation
1. Download the latest release `.zip` file
2. Open PhpStorm
3. Go to Settings → Plugins
4. Click the gear icon → Install Plugin from Disk
5. Select the downloaded `.zip` file
6. Restart PhpStorm

## Configuration

Go to **Settings → Tools → Livewire Component Folder**

### Scan Paths
Configure where to look for Livewire components (one path per line, relative to project root):
```
resources/views
resources/views/livewire
```

### Double-Click Action
Choose the default action when double-clicking a component folder.

### Files to Open
When using "Open selected files", check which file types you want to open.

### Split Layout
Choose how multiple files should be arranged when opened together.

## Example

**Before:**
```
pages/
  auth/
    login.blade.php
    login.php
  dashboard/
    index.blade.php
    index.php
    index.css
    index.js
```

**After:**
```
pages/
  ⚡ login
    .blade.php
    .php
  ⚡ index
    .blade.php
    .php
    .css
    .js
```

## Requirements

- PhpStorm 2024.3 or later
- Laravel with Livewire components

## Building from Source

```bash
# Build the plugin
./gradlew buildPlugin

# Run in sandbox IDE
./gradlew runIde

# Verify plugin
./gradlew verifyPlugin
```

The built plugin will be in `build/distributions/`.

## License

MIT License - see [LICENSE](LICENSE) file for details.

## Support

For issues and feature requests, please use the issue tracker on the plugin page.

---

Made with ⚡ for Laravel Livewire developers
