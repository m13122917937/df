[CmdletBinding()]
param(
    [ValidateSet('all', 'web', 'ui')]
    [string]$Target = 'all',
    [string]$RepositoryRoot = (Get-Location).Path,
    [switch]$ApproveOverwriteAndDelete,
    [switch]$DryRun
)

$ErrorActionPreference = 'Stop'
Set-StrictMode -Version Latest
$projects = @(
    [PSCustomObject]@{ Target = 'web'; Name = 'ruoyi-web'; Bucket = 'oss://wujievip/' },
    [PSCustomObject]@{ Target = 'ui'; Name = 'ruoyi-ui'; Bucket = 'oss://admin-wujievip-cn/' }
)
if ($Target -ne 'all') { $projects = @($projects | Where-Object { $_.Target -eq $Target }) }

$resolvedRoot = (Resolve-Path -LiteralPath $RepositoryRoot).Path
$npmCommand = Get-Command npm.cmd, npm -ErrorAction SilentlyContinue | Select-Object -First 1
if (-not $npmCommand) { throw '未找到 npm。' }
$ossCommand = Get-Command ossutil, ossutil64 -ErrorAction SilentlyContinue | Select-Object -First 1
if (-not $DryRun -and -not $ossCommand) { throw '未找到 ossutil；请先安装并运行 ossutil config。' }

foreach ($project in $projects) {
    $projectPath = Join-Path $resolvedRoot $project.Name
    foreach ($fileName in @('package.json', 'package-lock.json')) {
        $filePath = Join-Path $projectPath $fileName
        if (-not (Test-Path -LiteralPath $filePath -PathType Leaf)) { throw "缺少文件：$filePath" }
    }
}

foreach ($project in $projects) {
    $projectPath = Join-Path $resolvedRoot $project.Name
    Write-Host "正在构建 $($project.Name)..."
    Push-Location $projectPath
    try {
        & $npmCommand.Source run build:prod
        if ($LASTEXITCODE -ne 0) { throw "$($project.Name) 构建失败，退出码：$LASTEXITCODE" }
    }
    finally { Pop-Location }
    $distPath = Join-Path $projectPath 'dist'
    $indexPath = Join-Path $distPath 'index.html'
    if (-not (Test-Path -LiteralPath $indexPath -PathType Leaf)) { throw "构建产物校验失败：未找到 $indexPath" }
    if (-not (Get-ChildItem -LiteralPath $distPath -File -Recurse | Select-Object -First 1)) { throw "构建产物校验失败：$distPath 为空" }
    $project | Add-Member -NotePropertyName DistPath -NotePropertyValue $distPath -Force
}

if ($DryRun) {
    foreach ($project in $projects) { Write-Host "预检：$($project.DistPath) -> $($project.Bucket)" }
    Write-Host '构建与产物校验完成，未上传 OSS。'
    exit 0
}
if (-not $ApproveOverwriteAndDelete) { throw 'OSS 同步会覆盖同名对象并删除远端旧对象；请在获得明确授权后传入 -ApproveOverwriteAndDelete。' }

foreach ($project in $projects) {
    Write-Host "正在同步 $($project.Name) 到 $($project.Bucket)..."
    & $ossCommand.Source sync $project.DistPath $project.Bucket --force --delete
    if ($LASTEXITCODE -ne 0) { throw "$($project.Name) OSS 同步失败，退出码：$LASTEXITCODE" }
}
Write-Host "发布完成，目标：$Target"
