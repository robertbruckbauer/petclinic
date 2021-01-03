import { SvelteTypedComponent, SvelteAllProps } from 'svelte-typed-component';

export default class Chip extends SvelteTypedComponent<ChipProps, ChipEvents, ChipSlots> {
}

declare const _ChipProps: {

    /** 
     * Disabled state. 
     */
    disabled?: boolean;

    /** 
     * Material icon name.
     */
    icon?: string;

    /**
     * Title for tooltip
     */
    title?: string

} & SvelteAllProps;

declare const _ChipEvents: {
    click: MouseEvent;
    mouseover: MouseEvent;
    focus: FocusEvent;
    blur: FocusEvent;
};

declare const _ChipSlots: {
    default: {};
};

export declare type ChipProps = typeof _ChipProps;
export declare type ChipEvents = typeof _ChipEvents;
export declare type ChipSlots = typeof _ChipSlots;
export {
};
